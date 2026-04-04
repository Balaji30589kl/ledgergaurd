package com.balaji.ledgerguard.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.balaji.ledgerguard.dto.response.DashboardRecord;
import com.balaji.ledgerguard.dto.response.DashboardRecord.RecentActivityItem;
import com.balaji.ledgerguard.dto.response.TrendRecord;
import com.balaji.ledgerguard.entity.FinancialRecord;
import com.balaji.ledgerguard.enums.RecordType;
import com.balaji.ledgerguard.exception.InvalidOperationException;
import com.balaji.ledgerguard.repository.FinancialRecordRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository financialRecordRepository;

    public DashboardServiceImpl(FinancialRecordRepository financialRecordRepository) {
        this.financialRecordRepository = financialRecordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardRecord getSummary(LocalDate fromDate, LocalDate toDate) {
        DateRange dateRange = resolveDateRange(fromDate, toDate);

        BigDecimal totalIncome = safeAmount(financialRecordRepository
                .sumAmountByTypeAndDateRange(RecordType.INCOME, dateRange.fromDate(), dateRange.toDate()));
        BigDecimal totalExpense = safeAmount(financialRecordRepository
                .sumAmountByTypeAndDateRange(RecordType.EXPENSE, dateRange.fromDate(), dateRange.toDate()));

        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();
        List<Object[]> categoryRows = financialRecordRepository.findCategoryTotalsByDateRange(
                dateRange.fromDate(),
                dateRange.toDate()
        );
        for (Object[] row : categoryRows) {
            String category = (String) row[0];
            BigDecimal total = safeAmount((BigDecimal) row[1]);
            categoryTotals.put(category, total);
        }

        List<FinancialRecord> recentRecords = financialRecordRepository.findTop10ByDateRangeOrderByDateDesc(
                dateRange.fromDate(),
                dateRange.toDate()
        );
        List<RecentActivityItem> recentActivity = recentRecords.stream()
                .limit(10)
                .map(this::toRecentActivityItem)
                .toList();

        DashboardRecord summary = new DashboardRecord();
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpense(totalExpense);
        summary.setNetBalance(totalIncome.subtract(totalExpense));
        summary.setCategoryTotals(categoryTotals);
        summary.setRecentActivity(recentActivity);
        summary.setRecordCount(financialRecordRepository.countByDateRange(dateRange.fromDate(), dateRange.toDate()));
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrendRecord> getTrends(String period, LocalDate fromDate, LocalDate toDate) {
        DateRange dateRange = resolveDateRange(fromDate, toDate);
        String normalizedPeriod = normalizePeriod(period);

        List<Object[]> rows = switch (normalizedPeriod) {
            case "WEEK" -> financialRecordRepository.findWeeklyTrends(dateRange.fromDate(), dateRange.toDate());
            case "MONTH" -> financialRecordRepository.findMonthlyTrends(dateRange.fromDate(), dateRange.toDate());
            default -> throw new InvalidOperationException("period must be WEEK or MONTH");
        };

        return rows.stream()
                .map(this::toTrendRecord)
                .toList();
    }

    private TrendRecord toTrendRecord(Object[] row) {
        TrendRecord trend = new TrendRecord();
        BigDecimal income = safeAmount((BigDecimal) row[1]);
        BigDecimal expense = safeAmount((BigDecimal) row[2]);

        trend.setPeriod((String) row[0]);
        trend.setTotalIncome(income);
        trend.setTotalExpense(expense);
        trend.setNetBalance(income.subtract(expense));
        return trend;
    }

    private RecentActivityItem toRecentActivityItem(FinancialRecord record) {
        RecentActivityItem item = new RecentActivityItem();
        item.setId(record.getId());
        item.setAmount(record.getAmount());
        item.setType(record.getType());
        item.setCategory(record.getCategory());
        item.setDate(record.getDate());
        item.setNote(record.getNotes() == null ? "" : record.getNotes());
        return item;
    }

    private String normalizePeriod(String period) {
        if (period == null || period.isBlank()) {
            return "WEEK";
        }
        return period.trim().toUpperCase(Locale.ROOT);
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value;
    }

    private DateRange resolveDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null && toDate == null) {
            LocalDate today = LocalDate.now();
            return new DateRange(today.minusDays(30), today);
        }
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new InvalidOperationException("from must be before or equal to to");
        }
        return new DateRange(fromDate, toDate);
    }

    private record DateRange(LocalDate fromDate, LocalDate toDate) {
    }
}
