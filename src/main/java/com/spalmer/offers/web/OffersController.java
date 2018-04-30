package com.spalmer.offers.web;

import com.spalmer.offers.exception.InvalidOfferDataException;
import com.spalmer.offers.model.Offer;
import com.spalmer.offers.model.SearchDTO;
import com.spalmer.offers.service.OffersService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class OffersController {

	private final OffersService offersService;

	public OffersController(final OffersService offersService) {
		this.offersService = offersService;
	}

	@ExceptionHandler(InvalidOfferDataException.class)
	public @ResponseBody ResponseEntity<Map<String, String>> handleInvalidData(InvalidOfferDataException e) {

		return ResponseEntity.ok(Collections.singletonMap("message", e.getMessage()));
	}

	@RequestMapping(value = "offers", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Offer> createOffer(@RequestBody Offer offerRequest) {
		Offer offer = offersService.createOffer(offerRequest);
		return ResponseEntity.ok(offer);
	}

	@RequestMapping(value = "offers/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Offer>> getOfferById(@PathVariable Long id) {
		List<Offer> offerById = offersService.findOfferById(id);
		return ResponseEntity.ok(offerById);
	}

	@RequestMapping(value = "offers/search", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Offer>> searchOffer(SearchDTO dto) {
		List<Offer> offers = offersService.searchOffers(dto);
		return ResponseEntity.ok(offers);
	}

	@RequestMapping(value = "offers/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Long> cancelOffer(@PathVariable Long id) {
		offersService.cancelOffer(id);
		return ResponseEntity.ok(id);
	}

}
