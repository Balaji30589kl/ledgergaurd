package com.balaji.ledgerguard.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.balaji.ledgerguard.dto.response.DashboardRecord;
import com.balaji.ledgerguard.dto.response.TrendRecord;
import com.balaji.ledgerguard.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardRecord> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(dashboardService.getSummary(from, to));
    }

    @GetMapping("/trends")
    public ResponseEntity<List<TrendRecord>> getTrends(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(dashboardService.getTrends(period, from, to));
    }
}
