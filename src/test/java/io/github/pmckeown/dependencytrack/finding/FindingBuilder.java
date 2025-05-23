package io.github.pmckeown.dependencytrack.finding;

import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.fixAeroxeifeinComponent;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.fixMalkavineComponent;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.fixType1Vulnerability;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.fixType2Vulnerability;

public class FindingBuilder {

    private Component component;
    private Vulnerability vulnerability;
    private Analysis analysis;

    private FindingBuilder() {
        // Use builder factory method
    }

    public static FindingBuilder aFinding() {
        return new FindingBuilder();
    }

    public static FindingBuilder aDefaultFinding() {
        FindingBuilder findingBuilder = new FindingBuilder();
        findingBuilder.withComponent(aComponent());
        findingBuilder.withVulnerability(aVulnerability());
        findingBuilder.withAnalysis(anAnalysis());
        return findingBuilder;
    }

    public static FindingBuilder suppressedType1Finding() {
        FindingBuilder findingBuilder = new FindingBuilder();
        findingBuilder.withComponent(fixMalkavineComponent());
        findingBuilder.withVulnerability(fixType1Vulnerability());
        findingBuilder.withAnalysis(anAnalysis().withSuppressed(true));
        return findingBuilder;
    }

    public static FindingBuilder notSuppressedType2Finding() {
        FindingBuilder findingBuilder = new FindingBuilder();
        findingBuilder.withComponent(fixAeroxeifeinComponent());
        findingBuilder.withVulnerability(fixType2Vulnerability());
        findingBuilder.withAnalysis(anAnalysis());
        return findingBuilder;
    }

    public FindingBuilder withComponent(ComponentBuilder componentBuilder) {
        this.component = componentBuilder.build();
        return this;
    }

    public FindingBuilder withVulnerability(VulnerabilityBuilder vulnerabilityBuilder) {
        this.vulnerability = vulnerabilityBuilder.build();
        return this;
    }

    public FindingBuilder withAnalysis(AnalysisBuilder analysisBuilder) {
        this.analysis = analysisBuilder.build();
        return this;
    }

    public Finding build() {
        return new Finding(component, vulnerability, analysis);
    }
}
