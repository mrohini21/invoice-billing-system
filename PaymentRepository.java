package com.company.invoice.repository;

import com.company.invoice.model.Payment;
import com.company.invoice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByInvoice(Invoice invoice);

    // OR BETTER
    List<Payment> findByInvoiceId(Long invoiceId);

	void deleteByInvoiceId(Long id);
} 