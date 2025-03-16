package com.mtrifonov.quarkus.project.endpoints;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BaseExceptionHandler implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {

		int status = 500;

		if (exception instanceof WebApplicationException) {
			status = ((WebApplicationException) exception).getResponse().getStatus();
		}

		return Response.status(status).entity(exception.getMessage()).build();
	}

}
