package com.company.invoice.controller;
import com.company.invoice.model.Invoice;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.company.invoice.service.InvoiceService;

@RestController
@RequestMapping("/api/recurring-invoices")
public class RecurringInvoiceController {

    private final InvoiceService invoiceService;

    public RecurringInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public Invoice createRecurring(@RequestParam Long clientId,
                                   @RequestBody Invoice invoice) {

        // mark as recurring
        invoice.setIsRecurring(true);

        if (invoice.getRecurringFrequency() == null) { 
            throw new RuntimeException("Recurring frequency required");
        }

        return invoiceService.createInvoice(clientId, invoice);
    }

    @GetMapping
    public List<Invoice> getAll() {
        return invoiceService.getAllInvoices()
                .stream()
                .filter(Invoice::getIsRecurring)
                .toList();
    }


    @PutMapping("/{id}")
    public Invoice update(@PathVariable Long id,
                          @RequestBody Invoice updated) {
        return invoiceService.updateInvoice(id, updated);
    }

    @DeleteMapping("/{id}/stop")
    public void delete(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
    }

    @PostMapping("/{id}/generate")
    public Invoice generate(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoice(id);
        return invoiceService.generateRecurringInvoice(invoice);
    }
}
