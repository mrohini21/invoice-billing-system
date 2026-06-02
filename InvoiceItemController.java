package com.company.invoice.controller;

import com.company.invoice.model.InvoiceItem;
import com.company.invoice.service.InvoiceService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
 
import java.util.List; 


@RestController
@RequestMapping("/api/invoices")
public class InvoiceItemController {

    private final InvoiceService invoiceService;

    public InvoiceItemController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

 
    @PostMapping("/{invoiceId}/items")
    public InvoiceItem addItem(@PathVariable Long invoiceId,
                               @Valid @RequestBody InvoiceItem item) {
        return invoiceService.addItem(invoiceId, item);
    }

    @GetMapping("/{invoiceId}/items")
    public List<InvoiceItem> getItems(@PathVariable Long invoiceId) {
        return invoiceService.getItems(invoiceId);
    }
}