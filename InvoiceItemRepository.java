package com.company.invoice.repository;
import com.company.invoice.model.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    List<InvoiceItem> findByInvoice(Invoice invoice);

    // OR BETTER (recommended)
    List<InvoiceItem> findByInvoiceId(Long invoiceId);

	void deleteByInvoiceId(Long id);
}