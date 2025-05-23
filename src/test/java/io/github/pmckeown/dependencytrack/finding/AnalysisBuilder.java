package io.github.pmckeown.dependencytrack.finding;

public class AnalysisBuilder {

    private boolean suppressed = false;
    private AnalysisState state = null;

    private AnalysisBuilder() {
        // Use builder factory methods
    }

    public static AnalysisBuilder anAnalysis() {
        return new AnalysisBuilder();
    }

    public AnalysisBuilder withSuppressed(boolean s) {
        this.suppressed = s;
        return this;
    }

    public AnalysisBuilder withState(AnalysisState s) {
        this.state = s;
        return this;
    }

    public Analysis build() {
        return new Analysis(suppressed, state);
    }
}
