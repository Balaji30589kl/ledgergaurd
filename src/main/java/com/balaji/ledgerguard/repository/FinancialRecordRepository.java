package com.balaji.ledgerguard.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.balaji.ledgerguard.entity.FinancialRecord;
import com.balaji.ledgerguard.enums.RecordType;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>, JpaSpecificationExecutor<FinancialRecord> {

    @Query("""
	    select coalesce(sum(fr.amount), 0)
	    from FinancialRecord fr
	    where fr.type = :type
	    and (:fromDate is null or fr.date >= :fromDate)
	    and (:toDate is null or fr.date <= :toDate)
	    """)
    BigDecimal sumAmountByTypeAndDateRange(
	    @Param("type") RecordType type,
	    @Param("fromDate") LocalDate fromDate,
	    @Param("toDate") LocalDate toDate
    );

    @Query("""
	    select fr.category, coalesce(sum(fr.amount), 0)
	    from FinancialRecord fr
	    where (:fromDate is null or fr.date >= :fromDate)
	    and (:toDate is null or fr.date <= :toDate)
	    group by fr.category
	    """)
    List<Object[]> findCategoryTotalsByDateRange(
	    @Param("fromDate") LocalDate fromDate,
	    @Param("toDate") LocalDate toDate
    );

    @Query("""
	    select fr
	    from FinancialRecord fr
	    where (:fromDate is null or fr.date >= :fromDate)
	    and (:toDate is null or fr.date <= :toDate)
	    order by fr.date desc, fr.id desc
	    """)
    List<FinancialRecord> findTop10ByDateRangeOrderByDateDesc(
	    @Param("fromDate") LocalDate fromDate,
	    @Param("toDate") LocalDate toDate
    );

    @Query("""
	    select count(fr)
	    from FinancialRecord fr
	    where (:fromDate is null or fr.date >= :fromDate)
	    and (:toDate is null or fr.date <= :toDate)
	    """)
    long countByDateRange(
	    @Param("fromDate") LocalDate fromDate,
	    @Param("toDate") LocalDate toDate
    );

    @Query(value = """
	    select
		formatdatetime(fr.date, 'YYYY-ww') as period_key,
		coalesce(sum(case when fr.type = 'INCOME' then fr.amount else 0 end), 0) as total_income,
		coalesce(sum(case when fr.type = 'EXPENSE' then fr.amount else 0 end), 0) as total_expense
	    from financial_records fr
	    where (:fromDate is null or fr.date >= :fromDate)
	      and (:toDate is null or fr.date <= :toDate)
	    group by formatdatetime(fr.date, 'YYYY-ww')
	    order by period_key
	    """, nativeQuery = true)
    List<Object[]> findWeeklyTrends(
	    @Param("fromDate") LocalDate fromDate,
	    @Param("toDate") LocalDate toDate
    );

    @Query(value = """
	    select
		formatdatetime(fr.date, 'yyyy-MM') as period_key,
		coalesce(sum(case when fr.type = 'INCOME' then fr.amount else 0 end), 0) as total_income,
		coalesce(sum(case when fr.type = 'EXPENSE' then fr.amount else 0 end), 0) as total_expense
	    from financial_records fr
	    where (:fromDate is null or fr.date >= :fromDate)
	      and (:toDate is null or fr.date <= :toDate)
	    group by formatdatetime(fr.date, 'yyyy-MM')
	    order by period_key
	    """, nativeQuery = true)
    List<Object[]> findMonthlyTrends(
	    @Param("fromDate") LocalDate fromDate,
	    @Param("toDate") LocalDate toDate
    );
}
