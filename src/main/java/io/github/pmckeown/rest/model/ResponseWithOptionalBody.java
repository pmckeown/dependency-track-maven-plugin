package io.github.pmckeown.rest.model;

import java.util.Optional;

/**
 * Wrapper for the Get APIs with response bodies
 *
 * @author Paul McKeown
 */
public class ResponseWithOptionalBody<Body> extends Response {

    private final Optional<Body> body;

    public ResponseWithOptionalBody(int status, String statusText, boolean success, Optional<Body> body) {
        super(status, statusText, success);
        this.body = body;
    }

    public Optional<Body> getBody() {
        return this.body;
    }
}
