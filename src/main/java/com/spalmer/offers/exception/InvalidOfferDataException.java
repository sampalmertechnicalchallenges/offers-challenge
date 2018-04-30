package com.spalmer.offers.exception;

import java.util.List;
import java.util.stream.Collectors;

public class InvalidOfferDataException extends RuntimeException {

	public InvalidOfferDataException(List<String> missingFields) {
		super("The following fields are missing " + missingFields.stream().collect(Collectors.joining(",")));
	}
}
