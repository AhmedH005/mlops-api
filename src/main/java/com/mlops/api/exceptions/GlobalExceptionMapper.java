package com.mlops.api.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// catches anything not handled by a more specific mapper
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
	if (ex instanceof javax.ws.rs.WebApplicationException) {
    return ((javax.ws.rs.WebApplicationException) ex).getResponse();
}
        logger.log(Level.SEVERE, "unhandled exception: " + ex.getMessage(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("status", 500);
        body.put("error", "Internal Server Error");
        body.put("message", "something went wrong on the server");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
