package io.github.pmckeown.dependencytrack.finding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Analysis {

    public enum State {
        NOT_AFFECTED,
        FALSE_POSITIVE,
        IN_TRIAGE,
        EXPLOITABLE
    }

    private boolean isSuppressed;
    private State state;

    @JsonCreator
    public Analysis(@JsonProperty("isSuppressed") boolean isSuppressed, @JsonProperty("state") State state) {
        this.isSuppressed = isSuppressed;
        this.state = state;
    }

    public boolean isSuppressed() {
        return isSuppressed;
    }

    public State getState() {
        return state;
    }
}
