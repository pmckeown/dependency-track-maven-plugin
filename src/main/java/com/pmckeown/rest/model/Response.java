package com.pmckeown.rest.model;

public class Response {

    private final int status;
    private final String statusText;
    private boolean success;

    public Response(int status, String statusText, boolean success) {
        this.status = status;
        this.statusText = statusText;
        this.success = success;
    }

    public int getStatus() {
        return this.status;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public boolean isSuccess() {
        return this.success;
    }
}
