package io.github.pmckeown.rest.model;

/**
 * Wrapper for the Get APIs with response bodies
 *
 * @author Paul McKeown
 */
public class ResponseWithBody<Body> extends Response {

    private final Body body;

    public ResponseWithBody(int status, String statusText, boolean success, Body body) {
        super(status, statusText, success);
        this.body = body;
    }

    public Body getBody() {
        return this.body;
    }
}
