package com.crajas.gcpsamples.trace.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.crajas.gcpsamples.trace.model.Book;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Component
@Slf4j
public class BookService {

	public Book lookup(final String isbn) {
		
		Book book = null;
		if(isbn != null && isbn.length() >= 10 && isbn.length() <= 13) {
		
			try {
				
				RestTemplate restTemplate = new RestTemplate();
				
				String restURI = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn + "&country=US";
				ResponseEntity<String> json = restTemplate.getForEntity(restURI, String.class);
				if (json.getStatusCode() == HttpStatus.OK && checkIfAny(json.getBody())) {
					System.out.println(json.getBody());
					book = parseBook(json.getBody());
				} else {
					log.error(String.format("Book not found: %s, %s", json.getStatusCode(), json.toString()));
				}
	
			} catch (RestClientException | PathNotFoundException rce) {
				log.error(String.format("Book not found: %s", isbn), rce);
			}
		}

		return book;

	}

	public Book parseBook(String json) {
		Book book = new Book();
		Map <String, Object> volume = (Map<String, Object>)(JsonPath.read(json, "$..volumeInfo"));
		
		book.setTitle(String.valueOf(volume.get("title")));
		
		JSONArray authors = JsonPath.read(json, "$..authors");
		if (authors != null && authors.size() > 0) {
			List<String> authList = new ArrayList<>();
			for (int i = 0; i < authors.size(); ++i) {
				authList.add(String.valueOf(authors.get(i)));
			}
			book.setAuthors(authList);
		}

		String year = String.valueOf(volume.get("publishedDate"));
		if (year != null && year.length() >= 4) {
			book.setYearOfPublication(year);
		}

		book.setPublisher(String.valueOf(volume.get("publisher")));
		
		book.setIsbn(String.valueOf(JsonPath.read(json, "$..industryIdentifiers[?(@.type== 'ISBN_13')].identifier")));

		return book;
	}
	


	private boolean checkIfAny(String body) {
		Integer count = JsonPath.read(body, "$.totalItems");
		log.debug("Total items found: " + count);
		System.out.println("Total items found: " + count);
		return count > 0;
	}
}
