package com.mlops.api.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class LinkedWorkspaceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedWorkspaceNotFoundException> {

    @Override
    public Response toResponse(LinkedWorkspaceNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 422);
        body.put("error", "Unprocessable Entity");
        body.put("message", ex.getMessage());
        body.put("workspaceId", ex.getWorkspaceId());

        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
