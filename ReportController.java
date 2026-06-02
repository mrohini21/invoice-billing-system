package com.company.invoice.controller;
import java.util.*;
import java.math.BigDecimal;
import com.company.invoice.model.Invoice;
import com.company.invoice.service.ReportService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenue")
    public BigDecimal revenue() {
        return reportService.getTotalRevenue();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public List<Invoice> pending() {
        return reportService.getPendingInvoices();
    }
}