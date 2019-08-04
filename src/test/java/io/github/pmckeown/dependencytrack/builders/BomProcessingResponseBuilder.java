package io.github.pmckeown.dependencytrack.builders;

import io.github.pmckeown.dependencytrack.upload.BomProcessingResponse;

public class BomProcessingResponseBuilder {

    private boolean processing = false;

    private BomProcessingResponseBuilder() {
        // Use factory methods
    }

    public static BomProcessingResponseBuilder aBomProcessingResponse() {
        return new BomProcessingResponseBuilder();
    }

    public BomProcessingResponseBuilder withProcessing(boolean p) {
        this.processing = p;
        return this;
    }

    public BomProcessingResponse build() {
        return new BomProcessingResponse(processing);
    }
}
