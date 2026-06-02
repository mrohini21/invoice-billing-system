package com.company.invoice.service;

import org.springframework.stereotype.Service;
import com.company.invoice.model.*;
import com.company.invoice.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
 
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    public PaymentService(PaymentRepository paymentRepository,
                          InvoiceRepository invoiceRepository,
                          InvoiceService invoiceService) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
    }
    public Payment createPayment(Long invoiceId, BigDecimal amount, PaymentMethod method) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        BigDecimal totalPaid = paymentRepository.findByInvoice(invoice)
                .stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ❌ Prevent overpayment
        if (totalPaid.add(amount).compareTo(invoice.getTotalAmount()) > 0) {
            throw new RuntimeException("Payment exceeds invoice amount");
        }

        // ❌ Prevent paying recurring template
        if (invoice.getIsRecurring()) {
            throw new RuntimeException("Cannot pay a recurring template invoice");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(method);

        paymentRepository.save(payment);

        // ✅ FIXED HERE
        invoiceService.applyPayment(invoice, amount);

        return payment;
    }

    public List<Payment> getPaymentsByInvoice(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        return paymentRepository.findByInvoice(invoice);
    }
}