package com.balaji.ledgerguard.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.balaji.ledgerguard.enums.RecordType;

public class DashboardRecord {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> categoryTotals;
    private List<RecentActivityItem> recentActivity;
    private long recordCount;

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public Map<String, BigDecimal> getCategoryTotals() {
        return categoryTotals;
    }

    public void setCategoryTotals(Map<String, BigDecimal> categoryTotals) {
        this.categoryTotals = categoryTotals;
    }

    public List<RecentActivityItem> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<RecentActivityItem> recentActivity) {
        this.recentActivity = recentActivity;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public static class RecentActivityItem {
        private Long id;
        private BigDecimal amount;
        private RecordType type;
        private String category;
        private LocalDate date;
        private String note;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public RecordType getType() {
            return type;
        }

        public void setType(RecordType type) {
            this.type = type;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
}
