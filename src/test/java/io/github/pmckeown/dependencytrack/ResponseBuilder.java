package io.github.pmckeown.dependencytrack;

import java.util.Optional;

public class ResponseBuilder<T> {

    private int status;
    private String statusText;
    private boolean success;
    private T body;

    private ResponseBuilder(int status, String statusText, boolean success) {
        this.status = status;
        this.statusText = statusText;
        this.success = success;
    }

    public static <T> ResponseBuilder<T> aSuccessResponse() {
        return new ResponseBuilder<T>(200, "OK", true);
    }

    public static <T> ResponseBuilder<T> aNotFoundResponse() {
        return new ResponseBuilder<T>(404, "Not Found", false);
    }

    public ResponseBuilder<T> withBody(T b) {
        this.body = b;
        return this;
    }

    public Response<T> build() {
        return new Response<>(status, statusText, success, Optional.ofNullable(body));
    }
}
