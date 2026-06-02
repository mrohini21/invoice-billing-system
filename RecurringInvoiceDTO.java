package com.company.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.company.invoice.model.RecurringFrequency;

import jakarta.validation.constraints.NotNull;

public class RecurringInvoiceDTO {

    private Long clientId;
    private BigDecimal totalAmount;
    @NotNull(message = "Recurring frequency is required")
    private RecurringFrequency recurringFrequency;
    
    private LocalDateTime endDate; 
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public RecurringFrequency getRecurringFrequency() {
		return recurringFrequency;
	}
	public void setRecurringFrequency(RecurringFrequency recurringFrequency) {
		this.recurringFrequency = recurringFrequency;
	}
	public LocalDateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

  
}