package com.company.invoice.service;
import org.springframework.stereotype.Service;
import com.company.invoice.model.Invoice;
import com.company.invoice.model.InvoiceStatus;
import com.company.invoice.repository.*;
import java.math.BigDecimal;
import java.util.*;
	@Service
	public class ReportService {

	
	private final InvoiceRepository invoiceRepository;

	public ReportService(InvoiceRepository invoiceRepository) {
	    this.invoiceRepository = invoiceRepository;
	}
	
	public BigDecimal getTotalRevenue() {
	    return invoiceRepository.findByStatus(InvoiceStatus.PAID)
	            .stream()
	            .map(Invoice::getTotalAmount)
	            .filter(Objects::nonNull)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);
	}



	// Pending invoices
	public List<Invoice> getPendingInvoices() {
	    return invoiceRepository.findByStatus(InvoiceStatus.PENDING);
	}
	

}
