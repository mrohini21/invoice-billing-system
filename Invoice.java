package com.company.invoice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity 
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    @ManyToOne
    @JsonIgnore
    private Client client;

    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private LocalDateTime endDate;
    @PrePersist
    public void prePersist() {

        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();

        if (this.issueDate == null)
            this.issueDate = LocalDateTime.now();

        if (this.paidAmount == null)
            this.paidAmount = BigDecimal.ZERO;

        if (this.balanceAmount == null && this.totalAmount != null)
            this.balanceAmount = this.totalAmount;
    }
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    private RecurringFrequency recurringFrequency;

    private LocalDateTime nextGenerationDate;

    private LocalDateTime createdAt;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal paidAmount = BigDecimal.ZERO;
    private BigDecimal balanceAmount = BigDecimal.ZERO;
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public List<InvoiceItem> getItems() {
		return items;
	}

	public void setItems(List<InvoiceItem> items) {
		this.items = items;
	}
	
	
	@JsonManagedReference
	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	
	private List<InvoiceItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public LocalDateTime getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDateTime issueDate) {
		this.issueDate = issueDate;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public InvoiceStatus getStatus() {
		return status;
	} 

	public void setStatus(InvoiceStatus status) {
		this.status = status;
	}

	public Boolean getIsRecurring() {
		return isRecurring;
	}

	public void setIsRecurring(Boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public RecurringFrequency getRecurringFrequency() {
		return recurringFrequency;
	}

	public void setRecurringFrequency(RecurringFrequency recurringFrequency) {
		this.recurringFrequency = recurringFrequency;
	}

	public LocalDateTime getNextGenerationDate() {
		return nextGenerationDate;
	}

	public void setNextGenerationDate(LocalDateTime nextGenerationDate) {
		this.nextGenerationDate = nextGenerationDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
