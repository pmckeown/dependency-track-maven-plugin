package io.github.pmckeown.dependencytrack.suppressions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.pmckeown.dependencytrack.finding.Finding;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import io.github.pmckeown.dependencytrack.finding.Analysis.State;

public class Analysis {

    public enum AnalysisJustification {
        CODE_NOT_PRESENT,
        CODE_NOT_REACHABLE,
        REQUIRES_CONFIGURATION, REQUIRES_DEPENDENCY,
        REQUIRES_ENVIRONMENT,
        PROTECTED_BY_COMPILER,
        PROTECTED_AT_RUNTIME,
        PROTECTED_AT_PERIMETER,
        PROTECTED_BY_MITIGATING_CONTROL,
        NOT_SET
    }

    public enum AnalysisVendorResponse {
        CAN_NOT_FIX,
        WILL_NOT_FIX,
        UPDATE,
        ROLLBACK,
        WORKAROUND_AVAILABLE,
        NOT_SET
    }

    private String projectUuid;

    private String componentUuid;

    private String vulnerabilityUuid;

    private String analysisDetails;

    private State analysisState;

    private AnalysisJustification analysisJustification;

    private AnalysisVendorResponse analysisResponse;

    private boolean suppressed;

    private boolean isSuppressed;

    @JsonCreator
    public Analysis(
        @JsonProperty("project") String projectUuid,
        @JsonProperty("component") String componentUuid,
        @JsonProperty("vulnerability") String vulnerabilityUuid,
        @JsonProperty("analysisDetails") String analysisDetails,
        @JsonProperty("analysisState") State analysisState,
        @JsonProperty("analysisJustification") AnalysisJustification analysisJustification,
        @JsonProperty("analysisResponse") AnalysisVendorResponse analysisResponse,
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

    @XmlElement
    public String getProjectUuid() { return projectUuid; }

    @XmlElement
    public String getComponentUuid() { return componentUuid; }

    @XmlElement
    public String getVulnerabilityUuid() { return vulnerabilityUuid; }

    @XmlElement
    public String getAnalysisDetails() { return analysisDetails; }

    @XmlElement
    public State getAnalysisState() { return analysisState; }

    @XmlElement
    public AnalysisJustification getAnalysisJustification() { return analysisJustification; }

    @XmlElement
    public AnalysisVendorResponse getAnalysisResponse() { return analysisResponse; }

    @XmlElement
    public boolean getSuppressed() { return suppressed; }

    @XmlElement
    public boolean getIsSuppressed() { return isSuppressed; }

    public Boolean matchesFinding(Finding finding) {
        return finding.getVulnerability().getUuid().equals(getVulnerabilityUuid()) &&
            finding.getComponent().getUuid().equals(getComponentUuid());
    }

    @Override
    public String toString() { return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE); }
}
