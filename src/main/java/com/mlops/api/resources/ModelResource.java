package com.mlops.api.resources;

import com.mlops.api.exceptions.LinkedWorkspaceNotFoundException;
import com.mlops.api.models.MachineLearningModel;
import com.mlops.api.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/models")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ModelResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllModels(@QueryParam("status") String status) {
        List<MachineLearningModel> list = new ArrayList<>(store.getModels().values());

        // filter by status if provided
        if (status != null && !status.isBlank()) {
            list = list.stream()
                    .filter(m -> m.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        return Response.ok(list).build();
    }

    @POST
    public Response createModel(MachineLearningModel model) {
        if (model == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "request body missing");
            return Response.status(Response.Status.BAD_REQUEST).entity(err).build();
        }

        if (model.getWorkspaceId() == null || model.getWorkspaceId().isBlank()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "workspaceId is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(err).build();
        }

        // check workspace exists before registering the model
        if (!store.workspaceExists(model.getWorkspaceId())) {
            throw new LinkedWorkspaceNotFoundException(model.getWorkspaceId());
        }

        // server generates the id, client cant set it
        model.setId("MOD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        if (model.getStatus() == null || model.getStatus().isBlank()) {
            model.setStatus("TRAINING");
        }

        store.putModel(model);
        store.getWorkspaceById(model.getWorkspaceId()).addModelId(model.getId());

        Map<String, Object> res = new HashMap<>();
        res.put("message", "model registered");
        res.put("model", model);
        return Response.status(Response.Status.CREATED).entity(res).build();
    }

    @GET
    @Path("/{modelId}")
    public Response getById(@PathParam("modelId") String id) {
        MachineLearningModel model = store.getModelById(id);
        if (model == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "model not found");
            return Response.status(Response.Status.NOT_FOUND).entity(err).build();
        }
        return Response.ok(model).build();
    }

    // sub-resource locator for metrics
    @Path("/{modelId}/metrics")
    public EvaluationMetricResource getMetrics(@PathParam("modelId") String modelId) {
        return new EvaluationMetricResource(modelId);
    }
}
