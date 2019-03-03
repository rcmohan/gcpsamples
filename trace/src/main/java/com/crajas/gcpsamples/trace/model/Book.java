package com.crajas.gcpsamples.trace.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
	
	private String isbn;
	private String title;
	private List<String> authors;
	private String publisher;
	private String yearOfPublication;
	

}
