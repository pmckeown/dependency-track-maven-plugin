package io.github.pmckeown.dependencytrack.suppressions;

import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;

import io.github.pmckeown.dependencytrack.finding.Analysis.State;
import io.github.pmckeown.dependencytrack.finding.FindingBuilder;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.suppressions.Analysis.AnalysisJustification;
import io.github.pmckeown.dependencytrack.suppressions.Analysis.AnalysisVendorResponse;

public class AnalysisBuilder {

    private String projectUuid;

    private String componentUuid;

    private String vulnerabilityUuid;

    private String analysisDetails;

    private State analysisState;

    private AnalysisJustification analysisJustification;

    private AnalysisVendorResponse analysisResponse;

    private boolean suppressed;

    private boolean isSuppressed;

    private AnalysisBuilder() {
        // Use builder factory methods
    }

    public static AnalysisBuilder anAnalysis() {
        return new AnalysisBuilder();
    }

    public static AnalysisBuilder fixType1Analysis() {
        Project project = aProject().build();
        return new AnalysisBuilder()
            .withProjectUuid(project.getUuid())
            .withComponentUuid(FindingBuilder.suppressedType1Finding().build().getComponent().getUuid())
            .withVulnerabilityUuid(FindingBuilder.suppressedType1Finding().build().getVulnerability().getUuid())
            .withAnalysisDetails(VulnerabilitySuppressionBuilder.fixType1VulnerabilitySuppression().build().getAnalysisDetails())
            .withAnalysisState(VulnerabilitySuppressionBuilder.fixType1VulnerabilitySuppression().build().getAnalysisState())
            .withAnalysisJustification(VulnerabilitySuppressionBuilder.fixType1VulnerabilitySuppression().build().getAnalysisJustification())
            .withAnalysisResponse(VulnerabilitySuppressionBuilder.fixType1VulnerabilitySuppression().build().getAnalysisResponse());
    }

    public static AnalysisBuilder fixType2Analysis() {
        Project project = aProject().build();
        return new AnalysisBuilder()
            .withProjectUuid(project.getUuid())
            .withComponentUuid(FindingBuilder.notSuppressedType2Finding().build().getComponent().getUuid())
            .withVulnerabilityUuid(FindingBuilder.notSuppressedType2Finding().build().getVulnerability().getUuid())
            .withAnalysisDetails(VulnerabilitySuppressionBuilder.fixType2VulnerabilitySuppression().build().getAnalysisDetails())
            .withAnalysisState(VulnerabilitySuppressionBuilder.fixType2VulnerabilitySuppression().build().getAnalysisState())
            .withAnalysisJustification(VulnerabilitySuppressionBuilder.fixType2VulnerabilitySuppression().build().getAnalysisJustification())
            .withAnalysisResponse(VulnerabilitySuppressionBuilder.fixType2VulnerabilitySuppression().build().getAnalysisResponse());
    }

    public AnalysisBuilder withProjectUuid(String s) {
        this.projectUuid = s;
        return this;
    }

    public AnalysisBuilder withComponentUuid(String s) {
        this.componentUuid = s;
        return this;
    }

    public AnalysisBuilder withVulnerabilityUuid(String s) {
        this.vulnerabilityUuid = s;
        return this;
    }

    public AnalysisBuilder withAnalysisDetails(String s) {
        this.analysisDetails = s;
        return this;
    }

    public AnalysisBuilder withAnalysisState(State s) {
        this.analysisState = s;
        return this;
    }

    public AnalysisBuilder withAnalysisJustification(AnalysisJustification s) {
        this.analysisJustification = s;
        return this;
    }

    public AnalysisBuilder withAnalysisResponse(AnalysisVendorResponse s) {
        this.analysisResponse = s;
        return this;
    }

    public AnalysisBuilder withSuppressed(boolean s) {
        this.suppressed = s;
        this.isSuppressed = s;
        return this;
    }

    public Analysis build() {
        return new Analysis(projectUuid, componentUuid, vulnerabilityUuid, analysisDetails, analysisState,
            analysisJustification, analysisResponse, suppressed, isSuppressed);
    }
}
