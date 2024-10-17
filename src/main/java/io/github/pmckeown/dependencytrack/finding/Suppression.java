package io.github.pmckeown.dependencytrack.finding;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Suppression {

	private String cve;
	private String state = "NOT_AFFECTED";
	private String justification = "NOT_SET";
	private String details;

	public String getCve() {
		return cve;
	}

	public void setCve(final String cve) {
		this.cve = cve;
	}

	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(final String justification) {
		this.justification = justification;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(final String details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
