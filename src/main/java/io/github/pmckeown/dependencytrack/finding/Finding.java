package io.github.pmckeown.dependencytrack.finding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Finding {

    private Component component;
    private Vulnerability vulnerability;
    private Analysis analysis;

    @JsonCreator
    public Finding(@JsonProperty("component") Component component,
           @JsonProperty("vulnerability") Vulnerability vulnerability, @JsonProperty("analysis") Analysis analysis) {
        this.component = component;
        this.vulnerability = vulnerability;
        this.analysis = analysis;
    }

    public Component getComponent() {
        return component;
    }

    public Vulnerability getVulnerability() {
        return vulnerability;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
