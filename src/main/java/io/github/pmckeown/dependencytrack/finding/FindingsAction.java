package io.github.pmckeown.dependencytrack.finding;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.finding.Analysis.AnalysisJustification;
import io.github.pmckeown.dependencytrack.finding.Analysis.AnalysisState;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;

@Singleton
public class FindingsAction {

	private FindingsClient findingClient;

	private Logger logger;

	@Inject
	public FindingsAction(final FindingsClient findingClient, final Logger logger) {
		this.findingClient = findingClient;
		this.logger = logger;
	}

	public List<Finding> getFindings(final Project project) throws DependencyTrackException {
		logger.info("Getting findings for project %s-%s", project.getName(), project.getVersion());

		try {
			final Response<List<Finding>> response = findingClient.getFindingsForProject(project);
			final Optional<List<Finding>> body = response.getBody();
			if (response.isSuccess()) {
				if (body.isPresent()) {
					return body.get();
				} else {
					logger.info("No findings available for project %s-%s", project.getName(), project.getVersion());
					return Collections.emptyList();
				}
			} else {
				throw new DependencyTrackException("Error received from server");
			}
		} catch (final UnirestException ex) {
			logger.error(ex.getMessage());
			throw new DependencyTrackException(ex.getMessage());
		}
	}

	public void suppressFindings(final Project project, final List<Suppression> suppressions)
			throws DependencyTrackException {
		logger.info("Suppression list is present");
		final List<Finding> findings = getFindings(project);

		if (findings.isEmpty()) {
			logger.info("Skipping suppression");
		} else {
			suppressions.forEach((suppression) -> {
				final List<Finding> relevantFindings = findings.stream()
						.filter(finding -> Objects.equals(suppression.getCve(), finding.getVulnerability().getVulnId()))
						.collect(Collectors.toList());
				if (relevantFindings.isEmpty()) {
					logger.debug("Findings do not include vulnerability %s",
							Optional.ofNullable(suppression.getCve()).orElse("?"));
				} else {
					logger.info("Suppressing vulnerability %s", suppression.getCve());
					logger.debug("%s", suppression);

					final AnalysisState state = parseState(suppression.getState());
					relevantFindings.forEach(finding -> findingClient.recordNewAnalysisDecision(
							new AnalysisDecisionRequest(project.getUuid(), finding.getComponent().getUuid(),
									finding.getVulnerability().getUuid(), parseState(suppression.getState()),
									parseJustification(suppression.getJustification(), state), suppression.getDetails(),
									true)));
				}
			});
		}
	}

	private AnalysisState parseState(final String state) {
		try {
			return AnalysisState.valueOf(Objects.toString(state, "").toUpperCase());
		} catch (final RuntimeException ex) {
			return AnalysisState.NOT_AFFECTED;
		}
	}

	private AnalysisJustification parseJustification(final String justification, final AnalysisState state) {
		try {
			return AnalysisState.NOT_AFFECTED.equals(state)
					? AnalysisJustification.valueOf(Objects.toString(justification, "").toUpperCase())
					: AnalysisJustification.NOT_SET;
		} catch (final RuntimeException ex) {
			return AnalysisJustification.NOT_SET;
		}
	}

}
