package com.mlops.api.resources;

import com.mlops.api.exceptions.ModelDeprecatedException;
import com.mlops.api.models.EvaluationMetric;
import com.mlops.api.models.MachineLearningModel;
import com.mlops.api.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvaluationMetricResource {

    private final String modelId;
    private final DataStore store = DataStore.getInstance();

    public EvaluationMetricResource(String modelId) {
        this.modelId = modelId;
    }

    @GET
    public Response getMetrics() {
        MachineLearningModel model = store.getModelById(modelId);
        if (model == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "model not found");
            return Response.status(Response.Status.NOT_FOUND).entity(err).build();
        }

        List<EvaluationMetric> history = store.getMetricsForModel(modelId);
        return Response.ok(history).build();
    }

    @POST
    public Response addMetric(EvaluationMetric metric) {
        MachineLearningModel model = store.getModelById(modelId);

        if (model == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "model not found");
            return Response.status(Response.Status.NOT_FOUND).entity(err).build();
        }

        // deprecated models dont accept new metrics
        if ("DEPRECATED".equalsIgnoreCase(model.getStatus())) {
            throw new ModelDeprecatedException(modelId);
        }

        if (metric == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "request body missing");
            return Response.status(Response.Status.BAD_REQUEST).entity(err).build();
        }

        metric.setId(UUID.randomUUID().toString());
        metric.setTimestamp(System.currentTimeMillis());
        metric.setModelId(modelId);

        store.addMetricForModel(modelId, metric);

        // update parent models accuracy to the latest score
        model.setLatestAccuracy(metric.getAccuracyScore());

        Map<String, Object> res = new HashMap<>();
        res.put("message", "metric added");
        res.put("metric", metric);
        res.put("updatedAccuracy", model.getLatestAccuracy());
        return Response.status(Response.Status.CREATED).entity(res).build();
    }
}
