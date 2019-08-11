package io.github.pmckeown.dependencytrack;

import java.util.Optional;

public class ResponseBuilder {

    private int status;
    private String statusText;
    private boolean success;
    private Object body;

    private ResponseBuilder(int status, String statusText, boolean success) {
        this.status = status;
        this.statusText = statusText;
        this.success = success;
    }

    public static ResponseBuilder aSuccessResponse() {
        return new ResponseBuilder(200, "OK", true);
    }

    public static ResponseBuilder aNotFoundResponse() {
        return new ResponseBuilder(404, "Not Found", false);
    }

    public ResponseBuilder withBody(Object b) {
        this.body = b;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Response build() {
        return new Response(status, statusText, success, Optional.ofNullable(body));
    }
}
