package com.crajas.gcpsamples.trace.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.crajas.gcpsamples.trace.model.Book;
import com.crajas.gcpsamples.trace.service.BookService;

/**
 * REST end point that exposes a service to call an external services to lookup book.
 * 
 * @author rcmohan
 *
 */
@Component
@Path("/books")
public class BookResource {


	@Value("${gcp.project-id}")
	private String projectName;

	@Autowired
	BookService bookService;


	@GET
	@Produces("application/json")
	public Response getBook(@QueryParam("isbn") String isbn) {
		System.out.println("Looking up " + isbn);
		Book book = bookService.lookup(isbn);
		System.out.format("Book %s response: %s", isbn, book);
		return (book != null) ? Response.ok().entity(book).build() : Response.status(Status.NOT_FOUND).build();
	}

}