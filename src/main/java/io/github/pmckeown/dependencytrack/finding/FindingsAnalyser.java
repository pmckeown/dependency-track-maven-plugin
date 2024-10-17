package io.github.pmckeown.dependencytrack.finding;

import static io.github.pmckeown.dependencytrack.finding.Severity.CRITICAL;
import static io.github.pmckeown.dependencytrack.finding.Severity.HIGH;
import static io.github.pmckeown.dependencytrack.finding.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.Severity.MEDIUM;
import static io.github.pmckeown.dependencytrack.finding.Severity.UNASSIGNED;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.pmckeown.dependencytrack.Constants;
import io.github.pmckeown.util.Logger;

@Singleton
public class FindingsAnalyser {

	private static final String ERROR_TEMPLATE = "Number of %s issues [%d] exceeds the maximum allowed [%d]";

	private Logger logger;

	@Inject
	public FindingsAnalyser(final Logger logger) {
		this.logger = logger;
	}

	public boolean doNumberOfFindingsBreachPolicy(final List<Finding> findings,
			final FindingThresholds findingThresholds) {
		logger.info("Comparing findings against defined thresholds");

		if (findingThresholds == null) {
			return false;
		}

		boolean policyBreached = false;

		final long critical = getCount(findings, CRITICAL);
		final long high = getCount(findings, HIGH);
		final long medium = getCount(findings, MEDIUM);
		final long low = getCount(findings, LOW);
		final long unassigned = getCount(findings, UNASSIGNED);

		if (findingThresholds.getCritical() != null && critical > findingThresholds.getCritical()) {
			logger.warn(ERROR_TEMPLATE, Constants.CRITICAL, critical, findingThresholds.getCritical());
			policyBreached = true;
		}

		if (findingThresholds.getHigh() != null && high > findingThresholds.getHigh()) {
			logger.warn(ERROR_TEMPLATE, Constants.HIGH, high, findingThresholds.getHigh());
			policyBreached = true;
		}

		if (findingThresholds.getMedium() != null && medium > findingThresholds.getMedium()) {
			logger.warn(ERROR_TEMPLATE, Constants.MEDIUM, medium, findingThresholds.getMedium());
			policyBreached = true;
		}

		if (findingThresholds.getLow() != null && low > findingThresholds.getLow()) {
			logger.warn(ERROR_TEMPLATE, Constants.LOW, low, findingThresholds.getLow());
			policyBreached = true;
		}
		if (findingThresholds.getUnassigned() != null && unassigned > findingThresholds.getUnassigned()) {
			logger.warn(ERROR_TEMPLATE, Constants.UNASSIGNED, unassigned, findingThresholds.getUnassigned());
			policyBreached = true;
		}

		return policyBreached;
	}

	private long getCount(final List<Finding> findings, final Severity severity) {
		return findings.stream()
				.filter(f -> f.getVulnerability().getSeverity() == severity && !f.getAnalysis().isSuppressed()).count();
	}
}
