package io.github.pmckeown.dependencytrack.suppressions;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.finding.AnalysisState;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Gets all findings for the project and checks if the findings are suppressed or not.
 *
 * @author Thomas Hucke
 */
@Singleton
public class FindingsProcessor {

    private final FindingsAction findingsAction;

    private final Logger logger;

    @Inject
    public FindingsProcessor(FindingsAction findingsAction, Logger logger) {
        this.findingsAction = findingsAction;
        this.logger = logger;
    }

    Optional<VulnerabilitySuppression> getVulnerabilitySuppression(AbstractVulnerability finding,
        List<VulnerabilitySuppression> vulnerabilitySuppressions) {
        return vulnerabilitySuppressions.stream()
            .filter( v -> v.equals(finding)).findFirst();
    }

    private void allSuppressionsExistInFindingsCheck(
        List<? extends AbstractVulnerability> findings, List<? extends AbstractVulnerability> vulnerabilitySuppressions, boolean strictMode
    ) throws DependencyTrackException {
        if (strictMode) {
            logger.info("Strict mode is enabled, checking if all suppressions exist in findings");
            List<AbstractVulnerability> matchingVulnerabilitySuppressions =
                vulnerabilitySuppressions.stream()
                    .filter(v -> !findings.contains(v))
                    .collect(Collectors.toList());
            if (!matchingVulnerabilitySuppressions.isEmpty()) {
                matchingVulnerabilitySuppressions.forEach(v -> logger.error(
                    String.format("Vulnerability suppression %s does not exist in findings",
                        v.getVulnerabilityIdString())));
                throw new DependencyTrackException("Strict mode violation: not all suppressions exist in findings");
            }
        }
    }

    public List<Analysis> process(Project project, ModuleConfig moduleConfig, boolean strictMode)
        throws DependencyTrackException {
        List<Finding> findings = findingsAction.getFindings(project, true);
        logger.debug(String.format("Found %d findings", findings.size()));
        allSuppressionsExistInFindingsCheck(findings, moduleConfig.getVulnerabilitySuppressions(), strictMode);
        return findings.stream()
            .map(finding -> {
                Optional<VulnerabilitySuppression> vulnerabilitySuppression =
                    getVulnerabilitySuppression(finding, moduleConfig.getVulnerabilitySuppressions());
                if (vulnerabilitySuppression.isPresent() && !(finding.getAnalysis().getIsSuppressed())) {
                    logger.info(String.format("Suppressing %s finding for vulnerability %s on component %s",
                        finding.getVulnerability().getSeverity(), finding.getVulnerability().getVulnId(),
                        finding.getComponent().getName()));
                    // a suppression should be done but its currently missing
                    return new Analysis(
                        project.getUuid(),
                        finding.getComponent().getUuid(),
                        finding.getVulnerability().getUuid(),
                        vulnerabilitySuppression.get().getAnalysisDetails().trim().isEmpty() ?
                            "Activated by dependency-track-maven-plugin configuration" :
                            vulnerabilitySuppression.get().getAnalysisDetails(),
                        vulnerabilitySuppression.get().getAnalysisState(),
                        vulnerabilitySuppression.get().getAnalysisJustification(),
                        vulnerabilitySuppression.get().getAnalysisResponse(),
                        true,
                        true
                    );
                } else if ((!(vulnerabilitySuppression.isPresent()) && finding.getAnalysis().getIsSuppressed())) {
                    // a finding is suppressed but it should not - so remove it
                    logger.info(String.format("Reactivate %s finding for vulnerability %s on component %s",
                        finding.getVulnerability().getSeverity(), finding.getVulnerability().getVulnId(),
                        finding.getComponent().getName()));
                    return new Analysis(
                        project.getUuid(),
                        finding.getComponent().getUuid(),
                        finding.getVulnerability().getUuid(),
                        "Reactivated by dependency-track-maven-plugin",
                        AnalysisState.NOT_SET,
                        AnalysisJustificationEnum.NOT_SET,
                        AnalysisVendorResponseEnum.NOT_SET,
                        false,
                        false
                    );
                } else {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
