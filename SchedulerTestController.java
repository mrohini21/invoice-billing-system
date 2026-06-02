package com.company.invoice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.invoice.service.RecurringInvoiceScheduler;

@RestController
@RequestMapping("/api/test")
public class SchedulerTestController {

    private final RecurringInvoiceScheduler scheduler;

    public SchedulerTestController(RecurringInvoiceScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/run")
    public String runScheduler() {
        scheduler.generateRecurringInvoices();
        return "Scheduler triggered!";
    }
}