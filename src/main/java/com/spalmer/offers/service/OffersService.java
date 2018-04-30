package com.spalmer.offers.service;

import com.spalmer.offers.exception.InvalidOfferDataException;
import com.spalmer.offers.model.Offer;
import com.spalmer.offers.model.SearchDTO;
import com.spalmer.offers.repository.OffersRepository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static java.util.Objects.nonNull;

@Component
public class OffersService {

	private final OffersRepository repository;

	public OffersService(final OffersRepository repository) {
		this.repository = repository;
	}

	public List<Offer> searchOffers(SearchDTO dto) {
		return repository.findAll(new Specification<Offer>() {
			@Nullable
			@Override
			public Predicate toPredicate(Root<Offer> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if(nonNull(dto.getDescription())) {
					Predicate descriptionPredicate = cb.equal(root.get("description"), dto.getDescription());
					predicates.add(descriptionPredicate);
				}
				if(nonNull(dto.getAmount())) {
					Predicate amountPredicate = cb.equal(root.get("amount"), dto.getAmount());
					predicates.add(amountPredicate);
				}
				if(nonNull(dto.getCurrency())) {
					Predicate currencyPredicate = cb.equal(root.get("currency"), dto.getCurrency());
					predicates.add(currencyPredicate);
				}
				predicates.add(cb.greaterThan(root.get("expiry"), LocalDate.now()));
				predicates.add(cb.isFalse(root.get("cancelled")));
				return cb.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		});
	}

	public List<Offer> findOfferById(Long id) {
		return repository.findByIdAndExpiryGreaterThanEqualAndCancelledFalse(id, LocalDate.now());
	}

	public Offer createOffer(Offer offer) {
		if (!offer.getMissingFields().isEmpty()) {
			throw new InvalidOfferDataException(offer.getMissingFields());
		}
		offer.setCancelled(Boolean.FALSE);
		return repository.save(offer);
	}


	public void cancelOffer(Long id) {
		repository.findById(id)
				.ifPresent(this::setAsCancelled);
	}

	private void setAsCancelled(Offer offer) {
		offer.setCancelled(Boolean.TRUE);
		repository.save(offer);
	}
}
