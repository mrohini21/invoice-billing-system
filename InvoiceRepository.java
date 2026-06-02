package com.company.invoice.repository;
import com.company.invoice.model.Invoice;
import com.company.invoice.model.InvoiceItem;
import com.company.invoice.model.InvoiceStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByIsRecurringTrue();

	void deleteByClientId(Long clientId);

	List<Invoice> findByClientId(Long clientId);

	
}