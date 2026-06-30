package com.mlops.api.exceptions;

public class WorkspaceNotEmptyException extends RuntimeException {

    private final String workspaceId;
    private final int count;

    public WorkspaceNotEmptyException(String workspaceId, int count) {
        super("cannot delete workspace " + workspaceId + " it still has " + count + " model(s)");
        this.workspaceId = workspaceId;
        this.count = count;
    }

    public String getWorkspaceId() { return workspaceId; }
    public int getCount() { return count; }
}
