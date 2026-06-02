package com.company.invoice.service;

import org.springframework.stereotype.Service;

import com.company.invoice.exception.ResourceNotFoundException;
import com.company.invoice.model.*;
import com.company.invoice.repository.*;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.math.*;
@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final InvoiceItemRepository itemRepository;
    private final PaymentRepository paymentRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          ClientRepository clientRepository,
                          InvoiceItemRepository itemRepository,
                          PaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
        this.itemRepository = itemRepository;
        this.paymentRepository = paymentRepository;
    }

    public Invoice createInvoice(Long clientId, Invoice invoice) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        invoice.setClient(client);
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());

        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(7));

        // DO NOT override totalAmount if already provided
        if (invoice.getTotalAmount() == null) {
            invoice.setTotalAmount(BigDecimal.ZERO);
        }

        invoice.setPaidAmount(BigDecimal.ZERO);

        updateFinancialState(invoice);

        // ✅ Recurring logic
        if (Boolean.TRUE.equals(invoice.getIsRecurring())) {

            if (invoice.getRecurringFrequency() == null) {
                throw new RuntimeException("Recurring frequency required");
            }

            invoice.setNextGenerationDate(
                    calculateNextDate(invoice.getRecurringFrequency())
            );
        }

        return invoiceRepository.save(invoice);
    }

    // GET
    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // UPDATE
    public Invoice updateInvoice(Long id, Invoice updated) {

        Invoice invoice = getInvoice(id);

        if (updated.getDueDate() != null)
            invoice.setDueDate(updated.getDueDate());

        if (updated.getIsRecurring() != null)
            invoice.setIsRecurring(updated.getIsRecurring());

        if (updated.getRecurringFrequency() != null)
            invoice.setRecurringFrequency(updated.getRecurringFrequency());

        updateFinancialState(invoice);

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public void deleteInvoice(Long invoiceId) {

        Invoice invoice = getInvoice(invoiceId);

        // ❌ If payments exist → block delete
        if (!paymentRepository.findByInvoiceId(invoiceId).isEmpty()) {
            throw new RuntimeException("Cannot delete invoice with payments");
        }

        // ❌ If already paid → block
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Cannot delete PAID invoice");
        }
        if (invoice.getStatus() == InvoiceStatus.PARTIAL) {
            throw new RuntimeException("Cannot delete partially paid invoice");
        }
        // ✅ delete items first
        itemRepository.deleteByInvoiceId(invoiceId);

        // ✅ delete invoice
        invoiceRepository.deleteById(invoiceId);
    }

    // ADD ITEM
    public InvoiceItem addItem(Long invoiceId, InvoiceItem item) {

        Invoice invoice = getInvoice(invoiceId);

        if (item.getItemName() == null || item.getItemName().isBlank()) {
            throw new RuntimeException("Item name required");
        }
        item.setInvoice(invoice);
        item.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        itemRepository.save(item);

        recalculateTotal(invoice);

        return item;
    }

    // GET ITEMS
    public List<InvoiceItem> getItems(Long invoiceId) {
        return itemRepository.findByInvoiceId(invoiceId);
    }

    // RECALCULATE TOTAL
    private void recalculateTotal(Invoice invoice) {

        List<InvoiceItem> items = itemRepository.findByInvoiceId(invoice.getId());

        BigDecimal total = items.stream()
                .map(InvoiceItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setTotalAmount(total);

        updateFinancialState(invoice);

        invoiceRepository.save(invoice);
    }
   
    public void applyPayment(Invoice invoice, BigDecimal paidAmount) {

        BigDecimal existingPaid = invoice.getPaidAmount() == null
                ? BigDecimal.ZERO
                : invoice.getPaidAmount();

        invoice.setPaidAmount(existingPaid.add(paidAmount)); // ✅ ADD, not replace

        updateFinancialState(invoice);

        invoiceRepository.save(invoice);
    }

    // CORE FINANCIAL LOGIC
    public void updateFinancialState(Invoice invoice) {

        BigDecimal total = invoice.getTotalAmount() == null ? BigDecimal.ZERO : invoice.getTotalAmount();
        BigDecimal paid = invoice.getPaidAmount() == null ? BigDecimal.ZERO : invoice.getPaidAmount();

        invoice.setBalanceAmount(total.subtract(paid));

        if (paid.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PENDING);

        } else if (paid.compareTo(total) < 0) {

            if (invoice.getDueDate() != null &&
                invoice.getDueDate().isBefore(LocalDateTime.now())) {
                invoice.setStatus(InvoiceStatus.OVERDUE);
            } else {
                invoice.setStatus(InvoiceStatus.PARTIAL);
            }

        } else {
            invoice.setStatus(InvoiceStatus.PAID);
        }
    }

    public Invoice generateRecurringInvoice(Invoice old) {

        Invoice newInvoice = new Invoice();

        newInvoice.setClient(old.getClient());
        newInvoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        newInvoice.setIssueDate(LocalDateTime.now());
        newInvoice.setDueDate(LocalDateTime.now().plusDays(7));

        newInvoice.setPaidAmount(BigDecimal.ZERO);
        newInvoice.setStatus(InvoiceStatus.PENDING);
        newInvoice.setIsRecurring(false);

        Invoice savedInvoice = invoiceRepository.save(newInvoice);

        List<InvoiceItem> items = itemRepository.findByInvoiceId(old.getId());

        for (InvoiceItem item : items) {
            InvoiceItem newItem = new InvoiceItem();
            newItem.setItemName(item.getItemName());
            newItem.setPrice(item.getPrice());
            newItem.setQuantity(item.getQuantity());
            newItem.setInvoice(savedInvoice);
            newItem.setTotal(item.getTotal());

            itemRepository.save(newItem);
        }

        // ✅ RELOAD ITEMS FROM DB
        List<InvoiceItem> newItems =
                itemRepository.findByInvoiceId(savedInvoice.getId());

        savedInvoice.setItems(newItems);  // 🔥 THIS LINE FIXES YOUR ISSUE

        // ✅ recalculate
        recalculateTotal(savedInvoice);

        return savedInvoice;
    }

    
      
    // NEXT DATE
    public LocalDateTime calculateNextDate(RecurringFrequency freq) {

        return switch (freq) {
            case WEEKLY -> LocalDateTime.now().plusWeeks(1);
            case MONTHLY -> LocalDateTime.now().plusMonths(1);
            case QUARTERLY -> LocalDateTime.now().plusMonths(3);
            case YEARLY -> LocalDateTime.now().plusYears(1);
        };
    }
    @Transactional
    public void stopRecurring(Long id) {

        Invoice invoice = getInvoice(id);

        invoice.setIsRecurring(false);
        invoice.setRecurringFrequency(null);
        invoice.setNextGenerationDate(null);

        invoiceRepository.save(invoice);
    }
}
   