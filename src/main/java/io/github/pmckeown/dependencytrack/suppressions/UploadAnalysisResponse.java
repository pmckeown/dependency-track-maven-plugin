package io.github.pmckeown.dependencytrack.suppressions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import io.github.pmckeown.dependencytrack.finding.AnalysisState;

/**
 * POJO of the response from the Dependency-Track API for the upload analysis endpoint.
 *
 * @author Thomas Hucke
 */
public class UploadAnalysisResponse {

    private final AnalysisState analysisState;

    private final AnalysisJustificationEnum analysisJustification;

    private final AnalysisVendorResponseEnum analysisResponse;

    private final String analysisDetails;

    private final List<AnalysisComment> analysisComments;

    private final boolean isSuppressed;

    @JsonCreator
    public UploadAnalysisResponse(
        @JsonProperty("analysisState") AnalysisState analysisState,
        @JsonProperty("analysisJustification") AnalysisJustificationEnum analysisJustification,
        @JsonProperty("analysisResponse") AnalysisVendorResponseEnum analysisResponse,
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
    public AnalysisState getAnalysisState() { return analysisState; }

    @XmlElement
    public AnalysisJustificationEnum getAnalysisJustification() { return analysisJustification; }

    @XmlElement
    public AnalysisVendorResponseEnum getAnalysisResponse() { return analysisResponse; }

    @XmlElement
    public String getAnalysisDetails() { return analysisDetails; }

    @XmlElement
    public List<AnalysisComment> getAnalysisComments() { return analysisComments; }

    @XmlElement
    public boolean getIsSuppressed() { return isSuppressed; }

    @Override
    public String toString() { return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE); }
}
