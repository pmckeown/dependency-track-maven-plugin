package io.github.pmckeown.dependencytrack.finding;

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

    public FindingBuilder withComponent(ComponentBuilder componentBuilder) {
        this.component = componentBuilder.build();
        return this;
    }

    public FindingBuilder withVulnerability(VulnerabilityBuilder vulnerabilityBuilder) {
        this.vulnerability = vulnerabilityBuilder.build();
        return this;
    }

    public FindingBuilder withAnalysis(boolean isSuppressed) {
        this.analysis = new Analysis(isSuppressed, null);
        return this;
    }

    public Finding build() {
        return new Finding(component, vulnerability, analysis);
    }
}
