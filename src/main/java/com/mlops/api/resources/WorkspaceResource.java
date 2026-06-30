package com.mlops.api.resources;

import com.mlops.api.exceptions.WorkspaceNotEmptyException;
import com.mlops.api.models.MLWorkspace;
import com.mlops.api.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/workspaces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkspaceResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllWorkspaces() {
        List<MLWorkspace> list = new ArrayList<>(store.getWorkspaces().values());
        return Response.ok(list)
                .header("Cache-Control", "max-age=60")
                .build();
    }

    @POST
    public Response createWorkspace(MLWorkspace ws) {
        if (ws == null || ws.getId() == null || ws.getId().isBlank()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "id is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(err).build();
        }

        if (store.workspaceExists(ws.getId())) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "workspace already exists with that id");
            return Response.status(Response.Status.CONFLICT).entity(err).build();
        }

        if (ws.getModelIds() == null) {
            ws.setModelIds(new ArrayList<>());
        }

        store.putWorkspace(ws);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "workspace created");
        res.put("workspace", ws);
        return Response.status(Response.Status.CREATED).entity(res).build();
    }

    @GET
    @Path("/{workspaceId}")
    public Response getById(@PathParam("workspaceId") String id) {
        MLWorkspace ws = store.getWorkspaceById(id);
        if (ws == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "workspace not found");
            return Response.status(Response.Status.NOT_FOUND).entity(err).build();
        }
        return Response.ok(ws).build();
    }

    @HEAD
    @Path("/{workspaceId}")
    public Response checkExists(@PathParam("workspaceId") String id) {
        if (store.workspaceExists(id)) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{workspaceId}")
    public Response deleteWorkspace(@PathParam("workspaceId") String id) {
        MLWorkspace ws = store.getWorkspaceById(id);

        if (ws == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "workspace not found");
            return Response.status(Response.Status.NOT_FOUND).entity(err).build();
        }

        // cant delete if models are still assigned
        if (!ws.getModelIds().isEmpty()) {
            throw new WorkspaceNotEmptyException(id, ws.getModelIds().size());
        }

        store.deleteWorkspace(id);

        Map<String, String> res = new HashMap<>();
        res.put("message", "workspace deleted");
        return Response.ok(res).build();
    }
}
