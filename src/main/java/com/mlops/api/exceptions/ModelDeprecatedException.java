package com.mlops.api.exceptions;

public class ModelDeprecatedException extends RuntimeException {

    private final String modelId;

    public ModelDeprecatedException(String modelId) {
        super("model " + modelId + " is DEPRECATED, cannot add metrics");
        this.modelId = modelId;
    }

    public String getModelId() { return modelId; }
}
