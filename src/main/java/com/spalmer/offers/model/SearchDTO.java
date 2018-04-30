package com.spalmer.offers.model;

import java.math.BigDecimal;
import java.util.Currency;

public class SearchDTO {
	private String description;

	private Currency currency;

	private BigDecimal amount;

	public SearchDTO() {
	}

	public SearchDTO(String description, Currency currency, BigDecimal amount) {
		this.description = description;
		this.currency = currency;
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}

