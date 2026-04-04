package com.balaji.ledgerguard.service;

import java.time.LocalDate;
import java.util.List;

import com.balaji.ledgerguard.dto.response.DashboardRecord;
import com.balaji.ledgerguard.dto.response.TrendRecord;

public interface DashboardService {

    DashboardRecord getSummary(LocalDate fromDate, LocalDate toDate);

    List<TrendRecord> getTrends(String period, LocalDate fromDate, LocalDate toDate);
}
