package io.github.pmckeown.dependencytrack.finding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;

public class Analysis {

    private final boolean isSuppressed;
    private final AnalysisState state;

    @JsonCreator
    public Analysis(@JsonProperty("isSuppressed") boolean isSuppressed, @JsonProperty("state") AnalysisState state) {
        this.isSuppressed = isSuppressed;
        this.state = state;
    }

    @XmlElement
    public boolean getIsSuppressed() { return isSuppressed; }

    @XmlElement
    public AnalysisState getState() { return state; }

    @Override
    public String toString() { return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE); }
}
