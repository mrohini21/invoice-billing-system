package com.company.invoice.controller;

import com.company.invoice.model.Invoice;
import com.company.invoice.service.*;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PdfService pdfService;
    private final EmailService emailService;

    public InvoiceController(InvoiceService invoiceService,
                             PdfService pdfService,
                             EmailService emailService) {
        this.invoiceService = invoiceService;
        this.pdfService = pdfService;
        this.emailService = emailService;
    }

    @PostMapping
    public Invoice create(@RequestParam Long clientId,
                          @RequestBody Invoice invoice) {
        return invoiceService.createInvoice(clientId, invoice);
    }

    @GetMapping("/{id}")
    public Invoice get(@PathVariable Long id) {
        return invoiceService.getInvoice(id);
    }

    @GetMapping
    public List<Invoice> getAll() {
        return invoiceService.getAllInvoices();
    }

    @PutMapping("/{id}")
    public Invoice update(@PathVariable Long id,
                          @RequestBody Invoice invoice) {
        return invoiceService.updateInvoice(id, invoice);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
    }

  

    // 📄 DOWNLOAD PDF
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {

        Invoice invoice = invoiceService.getInvoice(id);
        byte[] pdf = pdfService.generateInvoicePdf(invoice);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice.pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }
    

    // 📧 SEND EMAIL
    @PostMapping("/{id}/email")
    public ResponseEntity<String> sendEmail(@PathVariable Long id) {

        Invoice invoice = invoiceService.getInvoice(id);
        byte[] pdf = pdfService.generateInvoicePdf(invoice);

        emailService.sendInvoice(invoice.getClient().getEmail(), pdf);

        return ResponseEntity.ok("Email sent successfully");
    }
}