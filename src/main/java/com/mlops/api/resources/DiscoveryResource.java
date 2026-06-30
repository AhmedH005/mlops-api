package com.mlops.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {
        Map<String, Object> info = new HashMap<>();
        info.put("apiName", "MLOps Pipeline Management API");
        info.put("version", "1.0.0");

        Map<String, String> contact = new HashMap<>();
        contact.put("name", "MLOps Platform Team");
        contact.put("email", "mlops-admin@ailab.example.com");
        info.put("contact", contact);

        Map<String, String> links = new HashMap<>();
        links.put("workspaces", "/api/v1/workspaces");
        links.put("models", "/api/v1/models");
        info.put("resources", links);

        return Response.ok(info).build();
    }
}
