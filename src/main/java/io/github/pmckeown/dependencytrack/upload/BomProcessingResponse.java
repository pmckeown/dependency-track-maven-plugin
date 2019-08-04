package io.github.pmckeown.dependencytrack.upload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BomProcessingResponse {

    private boolean processing;

    @JsonCreator
    public BomProcessingResponse(@JsonProperty("processing") boolean processing) {
        this.processing = processing;
    }

    public boolean isProcessing() {
        return processing;
    }
}
