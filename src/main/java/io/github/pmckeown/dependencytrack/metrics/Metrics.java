package io.github.pmckeown.dependencytrack.metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * Model class for the Project Metrics object
 *
 * @author Paul McKeown
 */
public class Metrics {

    private int inheritedRiskScore;
    private int critical;
    private int high;
    private int medium;
    private int low;
    private int unassigned;
    private int vulnerabilities;
    private int vulnerableComponents;
    private int components;
    private int suppressed;
    private int findingsTotal;
    private int findingsAudited;
    private int findingsUnaudited;
    private Date firstOccurrence;
    private Date lastOccurrence;

    @JsonCreator
    public Metrics(
            @JsonProperty("inheritedRiskScore") int inheritedRiskScore,
            @JsonProperty("critical") int critical,
            @JsonProperty("high") int high,
            @JsonProperty("medium") int medium,
            @JsonProperty("low") int low,
            @JsonProperty("unassigned") int unassigned,
            @JsonProperty("vulnerabilities") int vulnerabilities,
            @JsonProperty("vulnerableComponents") int vulnerableComponents,
            @JsonProperty("components") int components,
            @JsonProperty("suppressed") int suppressed,
            @JsonProperty("findingsTotal") int findingsTotal,
            @JsonProperty("findingsAudited") int findingsAudited,
            @JsonProperty("findingsUnaudited") int findingsUnaudited,
            @JsonProperty("firstOccurrence") Date firstOccurrence,
            @JsonProperty("lastOccurrence") Date lastOccurrence) {
        this.inheritedRiskScore = inheritedRiskScore;
        this.critical = critical;
        this.high = high;
        this.medium = medium;
        this.low = low;
        this.unassigned = unassigned;
        this.vulnerabilities = vulnerabilities;
        this.vulnerableComponents = vulnerableComponents;
        this.components = components;
        this.suppressed = suppressed;
        this.findingsTotal = findingsTotal;
        this.findingsAudited = findingsAudited;
        this.findingsUnaudited = findingsUnaudited;
        this.firstOccurrence = firstOccurrence;
        this.lastOccurrence = lastOccurrence;
    }

    public int getInheritedRiskScore() {
        return inheritedRiskScore;
    }

    public int getCritical() {
        return critical;
    }

    public int getHigh() {
        return high;
    }

    public int getMedium() {
        return medium;
    }

    public int getLow() {
        return low;
    }

    public int getUnassigned() {
        return unassigned;
    }

    public int getVulnerabilities() {
        return vulnerabilities;
    }

    public int getVulnerableComponents() {
        return vulnerableComponents;
    }

    public int getComponents() {
        return components;
    }

    public int getFindingsTotal() {
        return findingsTotal;
    }

    public int getFindingsAudited() {
        return findingsAudited;
    }

    public Date getFirstOccurrence() {
        return firstOccurrence;
    }

    public Date getLastOccurrence() {
        return lastOccurrence;
    }

    public int getIsSuppressed() {
        return suppressed;
    }

    public int getFindingsUnaudited() {
        return findingsUnaudited;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
