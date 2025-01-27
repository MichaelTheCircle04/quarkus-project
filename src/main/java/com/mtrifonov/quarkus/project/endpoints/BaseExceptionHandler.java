package com.mtrifonov.quarkus.project.endpoints;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

@Singleton
public class BaseExceptionHandler implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		System.out.println(exception.getMessage());
		return Response.status(400).entity(exception.getMessage()).build();
	}

}
