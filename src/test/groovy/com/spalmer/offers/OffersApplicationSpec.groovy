package com.spalmer.offers

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(classes = OffersApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OffersApplicationSpec extends Specification {

	@Value('${local.server.port}')
	private def port

	OffersClient client

	def setup() {
		client = new OffersClient(port)
	}

	def "When adding a new offer with all valid data supplied, I get a successful result"() {
		given:
			def offer = OffersClient.createValidOffer()
		when:
			def data = client.sendCreateOfferRequest(offer)
		then:
			data.id > 0
	}

	@Unroll
	def "When adding a new offer without #field, there is an error"() {
		given:
			def offer = OffersClient.createValidOffer()
			offer[field] = null
		when:
			def data = client.sendCreateOfferRequest(offer)
		then:
			data['message'] == "The following fields are missing $field"
		where:
			field << ['amount', 'currency', 'description', 'expiry']
	}

	@Unroll
	def "When retrieving an offer by id with a #time expiry date, the offer #returned returned"() {
		given:
			def offer = time == 'future' ? OffersClient.createValidOffer() : OffersClient.createExpiredOffer()
			def resp = client.sendCreateOfferRequest(offer)
		when:
			def getResponse = client.sendGetOfferByIdRequest(resp.id)
		then:
			if (returned == 'is')
				getResponse.id == resp.id
			else
				getResponse == []
		where:
			time     | returned
			'future' | 'is'
			'past'   | 'is not'
	}

	@Unroll
	def "When searching for an offer and there are #past with a past expiry date and #future with a future expiry date, #returned are returned"() {
		given:
			def oldOffers = client.sendSearchOfferRequest(['description' : 'Description'])
			for (oldOffer in oldOffers) {
				client.cancelOffer(oldOffer.id)
			}
			for (int i = 0; i < past; i++) {
				client.sendCreateOfferRequest(OffersClient.createExpiredOffer())
			}
			for (int i = 0; i < future; i++) {
				client.sendCreateOfferRequest(OffersClient.createValidOffer())
			}
		when:
			def response = client.sendSearchOfferRequest(['description': 'Description'])
		then:
			response.size() == returned
		where:
			past | future | returned
			  1  |    1   |     1
			  0  |    2   |     2
			  2  |    0   |     0
	}

	def "When I search for an offer, the correct offers are returned"() {
		given:
			def firstOffer = OffersClient.createValidOffer()
			firstOffer['currency'] = 'USD'
			def secondOffer = OffersClient.createValidOffer()
			def firstId = client.sendCreateOfferRequest(firstOffer).id
			client.sendCreateOfferRequest(secondOffer).id
		when:
			def searchResponse = client.sendSearchOfferRequest(['description' : 'Description', 'currency': 'USD'])
		then:
			searchResponse.size() == 1
			searchResponse[0].id == firstId
	}

	def "When I create an offer and then cancel it, it should not be returned in the search"() {
		given:
			def offer = OffersClient.createValidOffer()
			def create = client.sendCreateOfferRequest(offer)
		when:
			client.cancelOffer(create.id)
			def response = client.sendGetOfferByIdRequest(create.id)
		then:
			response.size() == 0
	}

}
