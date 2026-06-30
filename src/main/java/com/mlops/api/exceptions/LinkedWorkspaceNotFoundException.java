package com.mlops.api.exceptions;

public class LinkedWorkspaceNotFoundException extends RuntimeException {

    private final String workspaceId;

    public LinkedWorkspaceNotFoundException(String workspaceId) {
        super("workspaceId " + workspaceId + " does not exist");
        this.workspaceId = workspaceId;
    }

    public String getWorkspaceId() { return workspaceId; }
}
