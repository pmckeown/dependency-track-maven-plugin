package io.github.pmckeown.dependencytrack.finding;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Analysis {

	public enum AnalysisState {
		NOT_AFFECTED,
		FALSE_POSITIVE,
		IN_TRIAGE,
		EXPLOITABLE,
		NOT_SET,
		RESOLVED
	}

	public enum AnalysisJustification {
		CODE_NOT_PRESENT,
		CODE_NOT_REACHABLE,
		REQUIRES_CONFIGURATION,
		REQUIRES_DEPENDENCY,
		REQUIRES_ENVIRONMENT,
		PROTECTED_BY_COMPILER,
		PROTECTED_AT_RUNTIME,
		PROTECTED_AT_PERIMETER,
		PROTECTED_BY_MITIGATING_CONTROL,
		NOT_SET
	}

	private boolean isSuppressed;
	private AnalysisState analysisState;
	private AnalysisJustification analysisJustification;

	@JsonCreator
	public Analysis(@JsonProperty("isSuppressed") final boolean isSuppressed,
			@JsonProperty("analysisState") final AnalysisState analysisState,
			@JsonProperty("analysisJustification") final AnalysisJustification analysisJustification) {
		this.isSuppressed = isSuppressed;
		this.analysisState = analysisState;
		this.analysisJustification = analysisJustification;
	}

	@XmlElement
	public boolean isSuppressed() {
		return isSuppressed;
	}

	@XmlElement
	public AnalysisState getAnalysisState() {
		return analysisState;
	}

	@XmlElement
	public AnalysisJustification getAnalysisJustification() {
		return analysisJustification;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
