package com.spalmer.offers.repository;

import com.spalmer.offers.model.Offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

@Repository
public interface OffersRepository extends JpaRepository<Offer, Long>, JpaSpecificationExecutor<Offer> {

	List<Offer> findByIdAndExpiryGreaterThanEqualAndCancelledFalse(Long id, LocalDate now);
}
