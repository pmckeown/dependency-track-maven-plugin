package io.github.pmckeown.dependencytrack.builders;

import io.github.pmckeown.dependencytrack.upload.UploadBomResponse;

public class UploadBomResponseBuilder {

    private String token = "12345678-1234-1234-1234-123456789012";

    private UploadBomResponseBuilder() {
        // Use factory methods
    }

    public static UploadBomResponseBuilder anUploadBomResponse() {
        return new UploadBomResponseBuilder();
    }

    public UploadBomResponseBuilder withToken(String t) {
        this.token = t;
        return this;
    }

    public UploadBomResponse build() {
        return new UploadBomResponse(token);
    }
}
