package com.company.invoice.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.company.invoice.model.*;
import com.company.invoice.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecurringInvoiceScheduler {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    public RecurringInvoiceScheduler(InvoiceRepository invoiceRepository,
                                     InvoiceService invoiceService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
    }

    @Scheduled(cron = "0 0 0 * * ?") 
    public void generateRecurringInvoices() {

        List<Invoice> recurring = invoiceRepository.findByIsRecurringTrue();

        for (Invoice invoice : recurring) {

            if (invoice.getNextGenerationDate() == null) continue;

            if (!invoice.getNextGenerationDate().isAfter(LocalDateTime.now()) &&
                (invoice.getEndDate() == null || invoice.getEndDate().isAfter(LocalDateTime.now()))) {

                Invoice newInvoice = invoiceService.generateRecurringInvoice(invoice);

                // update next cycle
                switch (invoice.getRecurringFrequency()) {

                    case WEEKLY ->
                        invoice.setNextGenerationDate(invoice.getNextGenerationDate().plusWeeks(1));

                    case MONTHLY ->
                        invoice.setNextGenerationDate(invoice.getNextGenerationDate().plusMonths(1));

                    case QUARTERLY ->
                        invoice.setNextGenerationDate(invoice.getNextGenerationDate().plusMonths(3));

                    case YEARLY ->
                        invoice.setNextGenerationDate(invoice.getNextGenerationDate().plusYears(1));
                }

                invoiceRepository.save(invoice);
            }
        }
    }
}
