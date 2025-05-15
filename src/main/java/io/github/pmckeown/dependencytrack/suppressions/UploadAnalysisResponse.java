package io.github.pmckeown.dependencytrack.suppressions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import io.github.pmckeown.dependencytrack.finding.Analysis.State;
import io.github.pmckeown.dependencytrack.suppressions.Analysis.AnalysisJustification;
import io.github.pmckeown.dependencytrack.suppressions.Analysis.AnalysisVendorResponse;

public class UploadAnalysisResponse {

    private State analysisState;

    private AnalysisJustification analysisJustification;

    private AnalysisVendorResponse analysisResponse;

    private String analysisDetails;

    private List<AnalysisComment> analysisComments;

    private boolean isSuppressed;

    @JsonCreator
    public UploadAnalysisResponse(
        @JsonProperty("analysisState") State analysisState,
        @JsonProperty("analysisJustification") AnalysisJustification analysisJustification,
        @JsonProperty("analysisResponse") AnalysisVendorResponse analysisResponse,
        @JsonProperty("analysisDetails") String analysisDetails,
        @JsonProperty("analysisComments") List<AnalysisComment> analysisComments,
        @JsonProperty("isSuppressed") boolean isSuppressed
    ) {
        this.analysisState = analysisState;
        this.analysisJustification = analysisJustification;
        this.analysisResponse = analysisResponse;
        this.analysisDetails = analysisDetails;
        this.analysisComments = analysisComments;
        this.isSuppressed = isSuppressed;
    }

    @XmlElement
    public State getAnalysisState() { return analysisState; }

    @XmlElement
    public AnalysisJustification getAnalysisJustification() { return analysisJustification; }

    @XmlElement
    public AnalysisVendorResponse getAnalysisResponse() { return analysisResponse; }

    @XmlElement
    public String getAnalysisDetails() { return analysisDetails; }

    @XmlElement
    public List<AnalysisComment> getAnalysisComments() { return analysisComments; }

    @XmlElement
    public boolean getIsSuppressed() { return isSuppressed; }

    @Override
    public String toString() { return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE); }
}
