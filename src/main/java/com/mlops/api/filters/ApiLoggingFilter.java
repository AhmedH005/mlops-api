package com.mlops.api.filters;

import javax.ws.rs.container.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = Logger.getLogger(ApiLoggingFilter.class.getName());

    // logs before the request hits the resource method
    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        String method = req.getMethod();
        String uri = req.getUriInfo().getRequestUri().toString();
        String ct = req.getHeaderString("Content-Type");

        logger.info("[REQUEST] " + method + " " + uri + " | Content-Type: " + ct);
    }

    // logs after the response is built
    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException {
        int status = res.getStatus();
        String method = req.getMethod();
        String uri = req.getUriInfo().getRequestUri().toString();

        logger.info("[RESPONSE] " + status + " | " + method + " " + uri);
    }
}
