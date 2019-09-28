package io.github.pmckeown.dependencytrack.finding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;

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

    @XmlElement
    public boolean isSuppressed() {
        return isSuppressed;
    }

    @XmlElement
    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
