package io.github.pmckeown.dependencytrack.suppressions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pmckeown.dependencytrack.finding.Finding;
import javax.xml.bind.annotation.XmlElement;
import io.github.pmckeown.dependencytrack.finding.AnalysisState;

/**
 * POJO representaion of an analysis request payload.
 *
 * @author Thomas Hucke
 */
public class Analysis {

    private final String projectUuid;

    private final String componentUuid;

    private final String vulnerabilityUuid;

    private final String analysisDetails;

    private final AnalysisState analysisState;

    private final AnalysisJustificationEnum analysisJustification;

    private final AnalysisVendorResponseEnum analysisResponse;

    private final boolean suppressed;

    private final boolean isSuppressed;

    @JsonCreator
    public Analysis (
        @JsonProperty("project") String projectUuid,
        @JsonProperty("component") String componentUuid,
        @JsonProperty("vulnerability") String vulnerabilityUuid,
        @JsonProperty("analysisDetails") String analysisDetails,
        @JsonProperty("analysisState") AnalysisState analysisState,
        @JsonProperty("analysisJustification") AnalysisJustificationEnum analysisJustification,
        @JsonProperty("analysisResponse") AnalysisVendorResponseEnum analysisResponse,
        @JsonProperty("suppressed") boolean suppressed,
        @JsonProperty("isSuppressed") boolean isSuppressed
    ) {
        this.projectUuid = projectUuid;
        this.componentUuid = componentUuid;
        this.vulnerabilityUuid = vulnerabilityUuid;
        this.analysisDetails = analysisDetails;
        this.analysisState = analysisState;
        this.analysisJustification = analysisJustification;
        this.analysisResponse = analysisResponse;
        this.isSuppressed = isSuppressed;
        this.suppressed = suppressed;
    }

    @JsonGetter("project")
    @XmlElement
    public String getProjectUuid() { return projectUuid; }

    @JsonGetter("component")
    @XmlElement
    public String getComponentUuid() { return componentUuid; }

    @JsonGetter("vulnerability")
    @XmlElement
    public String getVulnerabilityUuid() { return vulnerabilityUuid; }

    @XmlElement
    public String getAnalysisDetails() { return analysisDetails; }

    @XmlElement
    public AnalysisState getAnalysisState() { return analysisState; }

    @XmlElement
    public AnalysisJustificationEnum getAnalysisJustification() { return analysisJustification; }

    @XmlElement
    public AnalysisVendorResponseEnum getAnalysisResponse() { return analysisResponse; }

    @XmlElement
    public boolean getSuppressed() { return suppressed; }

    @XmlElement
    public boolean getIsSuppressed() { return isSuppressed; }

    public Boolean matchesFinding(Finding finding) {
        return finding.getVulnerability().getUuid().equals(getVulnerabilityUuid()) &&
            finding.getComponent().getUuid().equals(getComponentUuid());
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
