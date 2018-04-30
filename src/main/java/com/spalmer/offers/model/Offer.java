package com.spalmer.offers.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static java.util.Objects.isNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Offer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String description;

	private Currency currency;

	private BigDecimal amount;

	@JsonFormat(pattern =   "yyyy-MM-dd")
	private LocalDate expiry;

	private Boolean cancelled;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDate getExpiry() {
		return expiry;
	}

	public void setExpiry(LocalDate expiry) {
		this.expiry = expiry;
	}

	public Boolean getCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	public List<String> getMissingFields() {
		List<String> missingFields = new ArrayList<>();
		if (isNull(this.description)) {
			missingFields.add("description");
		}
		if (isNull(this.currency)) {
			missingFields.add("currency");
		}
		if (isNull(this.amount)) {
			missingFields.add("amount");
		}
		if (isNull(this.expiry)) {
			missingFields.add("expiry");
		}
		return missingFields;
	}
}
