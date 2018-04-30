package com.spalmer.offers

import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

import java.time.LocalDate

class OffersClient {
	def client

	OffersClient(port) {
		client = new RESTClient("http://localhost:$port/")
		client.handler.failure = { resp, data ->
			resp.data = data
			return resp
		}
	}

	static def createValidOffer() {
		def offer = [:]
		offer['amount'] = 123.12
		offer['currency'] = 'GBP'
		offer['description'] = 'Description'
		offer['expiry'] = LocalDate.now().plusDays(10).toString();
		return offer
	}

	static def createExpiredOffer() {
		def offer = createValidOffer()
		offer['expiry'] = LocalDate.now().minusDays(1).toString()
		return offer
	}

	def sendCreateOfferRequest(body) {
		def resp = client.post(
				'path': 'offers',
				'requestContentType': ContentType.JSON,
				'body' : body
		)
		return resp.responseData
	}

	def sendGetOfferByIdRequest(id) {
		def resp = client.get(
				"path": "offers/$id"
		)
		return resp.responseData
	}

	def sendSearchOfferRequest(params) {
		def resp = client.get(
				"path": "offers/search",
				"query" : params
		)
		return resp.responseData
	}

	def cancelOffer(id) {
		def resp = client.delete(
				"path" : "offers/$id"
		)
		return resp.responseData
	}
}
