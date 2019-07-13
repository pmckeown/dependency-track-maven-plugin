package io.github.pmckeown.dependencytrack.metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Model class for the Project Metrics object
 *
 * @author Paul McKeown
 */
public class Metrics {

    private int inheritedRiskScore;

    @JsonCreator
    public Metrics(@JsonProperty("inheritedRiskScore") int inheritedRiskScore) {
        this.inheritedRiskScore = inheritedRiskScore;
    }

    public int getInheritedRiskScore() {
        return inheritedRiskScore;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
