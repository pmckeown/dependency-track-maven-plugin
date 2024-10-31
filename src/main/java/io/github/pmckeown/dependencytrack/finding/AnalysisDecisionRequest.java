package io.github.pmckeown.dependencytrack.finding;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.github.pmckeown.dependencytrack.finding.Analysis.AnalysisJustification;
import io.github.pmckeown.dependencytrack.finding.Analysis.AnalysisState;

public class AnalysisDecisionRequest {

	/*
	 * For project, component and vulnerability the according UUID must be used
	 */
	private String project;
	private String component;
	private String vulnerability;
	private AnalysisState analysisState;
	private AnalysisJustification analysisJustification;
	private String analysisDetails;
	private boolean isSuppressed;

	public AnalysisDecisionRequest(final String project, final String component, final String vulnerability,
			final AnalysisState analysisState, final AnalysisJustification analysisJustification,
			final String analysisDetails, final boolean isSuppressed) {
		this.project = project;
		this.component = component;
		this.vulnerability = vulnerability;
		this.analysisState = analysisState;
		this.isSuppressed = isSuppressed;
		this.analysisJustification = analysisJustification;
		this.analysisDetails = analysisDetails;
	}

	public String getProject() {
		return project;
	}

	public String getComponent() {
		return component;
	}

	public String getVulnerability() {
		return vulnerability;
	}

	public AnalysisState getAnalysisState() {
		return analysisState;
	}

	public AnalysisJustification getAnalysisJustification() {
		return analysisJustification;
	}

	public String getAnalysisDetails() {
		return analysisDetails;
	}

	public boolean isSuppressed() {
		return isSuppressed;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
