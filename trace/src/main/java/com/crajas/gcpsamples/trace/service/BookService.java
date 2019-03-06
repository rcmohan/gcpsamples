package com.crajas.gcpsamples.trace.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.crajas.gcpsamples.trace.db.BookRepository;
import com.crajas.gcpsamples.trace.model.Book;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Component
@Slf4j
public class BookService {
	
	@Autowired
	private BookRepository bookRepository;

	public Book lookup(final String isbn) {
		
		Book book = null;
		if(isbn != null && isbn.length() >= 10 && isbn.length() <= 13) {

			Optional<Book> bookO = bookRepository.findById(isbn);
			if(bookO.isPresent()) {
				System.out.println("Found in redis!");
				book = bookO.get();
			} else {
				try {
					
					RestTemplate restTemplate = new RestTemplate();
					
					String restURI = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn + "&country=US";
					ResponseEntity<String> json = restTemplate.getForEntity(restURI, String.class);
					if (json.getStatusCode() == HttpStatus.OK && getCount(json.getBody()) > 0) {
						
						book = parseBook(json.getBody());
						book = bookRepository.save(book);
						System.out.println("Saved to redis");
					} else {
						log.error(String.format("Book not found: %s, %s", json.getStatusCode(), json.toString()));
					}
		
				} catch (RestClientException | PathNotFoundException rce) {
					log.error(String.format("Book not found: %s", isbn), rce);
				}
			}
		}

		return book;

	}

	public Book parseBook(String json) {
		Book book = new Book();
		
		book.setTitle(extract(json, "$.items[0].volumeInfo.title"));
		
		JSONArray authors = JsonPath.read(json, "$.items[0].volumeInfo.authors");
		if (authors != null && authors.size() > 0) {
			List<String> authList = new ArrayList<>();
			for (int i = 0; i < authors.size(); ++i) {
				Object ith = authors.get(i);
				System.out.println(ith.getClass());
				authList.add(String.valueOf(ith));
			}
			book.setAuthors(authList);
		}

		String year = extract(json, "$.items[0].volumeInfo.publishedDate");
		System.out.println(year);
		if (year != null && year.length() >= 4) {
			book.setYearOfPublication(year);
		}

		book.setPublisher(extract(json, "$.items[0].volumeInfo.publisher"));
		
		book.setIsbn(extract(json, "$.items[0].volumeInfo.industryIdentifiers[?(@.type== 'ISBN_13')].identifier"));

		return book;
	}

	private String extract(String json, String path) {
		Object value = JsonPath.read(json, path);
		if(value instanceof JSONArray) {
			value = ((JSONArray) value).get(0);
		}
		String valueStr = String.valueOf(value);
		return valueStr;
	}
	


	private int getCount(String body) {
		Integer count = JsonPath.read(body, "$.totalItems");
		log.debug("Total items found: " + count);
		System.out.println("Total items found: " + count);
		return count;
	}
}
