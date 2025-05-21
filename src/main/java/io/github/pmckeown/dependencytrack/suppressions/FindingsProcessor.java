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

    private Boolean doesSuppressionAffectFinding(Finding finding, VulnerabilitySuppression vulnerabilitySuppression) {
        return finding.getVulnerability().getSource().equals(vulnerabilitySuppression.getSource()) &&
            finding.getVulnerability().getVulnId().equals(vulnerabilitySuppression.getVulnId());
    }

    Optional<VulnerabilitySuppression> getVulnerabilitySuppression(Finding finding,
        List<VulnerabilitySuppression> vulnerabilitySuppressions) {
        return vulnerabilitySuppressions.stream()
            .filter(v -> doesSuppressionAffectFinding(finding, v)).findFirst();
    }

    public List<Analysis> process(Project project, ModuleConfig moduleConfig) throws DependencyTrackException {
        List<Finding> findings = findingsAction.getFindings(project, true);
        logger.debug(String.format("Found %d findings", findings.size()));
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
                        moduleConfig.getProjectUuid(),
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
