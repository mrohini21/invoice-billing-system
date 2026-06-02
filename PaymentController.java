package com.company.invoice.controller;

import com.company.invoice.dto.PaymentDTO;
import com.company.invoice.model.Payment;
import com.company.invoice.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;
 
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment createPayment(@Valid @RequestBody PaymentDTO request) {

        return paymentService.createPayment(
                request.getInvoiceId(),
                request.getAmount(),
                request.getMethod()
        );
    }
    @GetMapping("/{invoiceId}")
    public List<Payment> getPayments(@PathVariable Long invoiceId) {
        return paymentService.getPaymentsByInvoice(invoiceId);
    }
}