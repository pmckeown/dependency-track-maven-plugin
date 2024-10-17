package io.github.pmckeown.dependencytrack.finding;

public class AnalysisBuilder {

	private boolean suppressed = false;
	private Analysis.AnalysisState state = null;
	private Analysis.AnalysisJustification justification = null;

	private AnalysisBuilder() {
		// Use builder factory methods
	}

	public static AnalysisBuilder anAnalysis() {
		return new AnalysisBuilder();
	}

	public AnalysisBuilder withSuppressed(final boolean s) {
		suppressed = s;
		return this;
	}

	public AnalysisBuilder withState(final Analysis.AnalysisState s) {
		state = s;
		return this;
	}

	public AnalysisBuilder withState(final Analysis.AnalysisJustification j) {
		justification = j;
		return this;
	}

	public Analysis build() {
		return new Analysis(suppressed, state, justification);
	}
}
