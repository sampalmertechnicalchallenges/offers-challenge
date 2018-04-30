package com.spalmer.offers;

import com.spalmer.offers.exception.InvalidOfferDataException;
import com.spalmer.offers.model.Offer;
import com.spalmer.offers.model.SearchDTO;
import com.spalmer.offers.repository.OffersRepository;
import com.spalmer.offers.service.OffersService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OffersServiceTest {

	private OffersRepository offersRepository;

	private OffersService offersService;

	@Before
	public void setUp() {
		offersRepository = Mockito.mock(OffersRepository.class);
		Mockito.reset(offersRepository);
		offersService = new OffersService(this.offersRepository);
	}

	@Test
	public void testSearchOffers() {

		List<Offer> offers = new ArrayList<>();
		LocalDate future = LocalDate.now().plusDays(1);
		LocalDate past = LocalDate.now().minusDays(1);

		when(this.offersRepository.findAll(any(Specification.class)))
				.thenReturn(Collections.singletonList(getOffer(3L, future, Boolean.FALSE)));

		List<Offer> results = offersService.searchOffers(new SearchDTO("Description", null, null));
		assertEquals(results.size(), 1);
		assertEquals(results.get(0).getId(), Long.valueOf(3L));
		verify(offersRepository, times(1)).findAll(any(Specification.class));
	}

	@Test
	public void testFindOfferById() {
		LocalDate future = LocalDate.now().plusDays(1);
		LocalDate past = LocalDate.now().minusDays(1);
		LocalDate now = LocalDate.now();

		when(this.offersRepository.findByIdAndExpiryGreaterThanEqualAndCancelledFalse(1L, now))
				.thenReturn(Collections.emptyList());

		when(this.offersRepository.findByIdAndExpiryGreaterThanEqualAndCancelledFalse(2L, now))
				.thenReturn(Collections.emptyList());

		when(this.offersRepository.findByIdAndExpiryGreaterThanEqualAndCancelledFalse(3L, now))
				.thenReturn(Collections.singletonList(getOffer(3L, future, Boolean.FALSE)));

		assertEquals(offersService.findOfferById(1L).size(), 0);
		assertEquals(offersService.findOfferById(2L).size(), 0);
		assertEquals(offersService.findOfferById(3L).size(), 1);

		verify(offersRepository, times(1)).findByIdAndExpiryGreaterThanEqualAndCancelledFalse(1L, now);
		verify(offersRepository, times(1)).findByIdAndExpiryGreaterThanEqualAndCancelledFalse(2L, now);
		verify(offersRepository, times(1)).findByIdAndExpiryGreaterThanEqualAndCancelledFalse(3L, now);
	}

	@Test
	public void testCreateOffers() {
		try {
			Offer offer = new Offer();
			offer.setDescription("Test");
			offersService.createOffer(offer);
		} catch(InvalidOfferDataException e) {
			assertEquals(e.getMessage(), "The following fields are missing currency,amount,expiry");
		}

		Offer validOffer = getOffer(null, LocalDate.now().plusDays(1L), Boolean.FALSE);
		Offer returnOffer = getOffer(1L, LocalDate.now().plusDays(1L), Boolean.FALSE);
		when(offersRepository.save(any())).thenReturn(returnOffer);
		Offer newOffer = offersService.createOffer(validOffer);
		verify(offersRepository, atMost(1)).save(validOffer);
		assertNotNull(newOffer.getId());
		assertFalse(newOffer.getCancelled());
	}

	@Test
	public void testCancelOffers() {
		Offer validOffer = getOffer(null, LocalDate.now().plusDays(1L), Boolean.FALSE);
		when(offersRepository.save(any())).thenReturn(validOffer);
		when(offersRepository.findById(1L)).thenReturn(Optional.of(validOffer));
		validOffer.setCancelled(Boolean.TRUE);
		offersService.cancelOffer(1L);
		verify(offersRepository, times(1)).save(validOffer);
	}

	private Offer getOffer(Long id, LocalDate past, Boolean cancelled) {
		Offer offer = new Offer();
		offer.setId(id);
		offer.setAmount(BigDecimal.valueOf(1234.12));
		offer.setCurrency(Currency.getInstance("GBP"));
		offer.setDescription("Description");
		offer.setExpiry(past);
		offer.setCancelled(cancelled);
		return offer;
	}

}
