package com.crajas.gcpsamples.trace.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Book")
public class Book implements Serializable {
	
	
	@Id private String isbn;
	private String title;
	private List<String> authors;
	private String publisher;
	private String yearOfPublication;
	private String description;

}
