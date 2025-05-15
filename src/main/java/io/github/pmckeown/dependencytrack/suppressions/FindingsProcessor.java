package io.github.pmckeown.dependencytrack.suppressions;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.finding.Analysis.State;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.suppressions.Analysis.AnalysisJustification;
import io.github.pmckeown.dependencytrack.suppressions.Analysis.AnalysisVendorResponse;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;

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
        Set<VulnerabilitySuppression> vulnerabilitySuppressions) {
        return vulnerabilitySuppressions.stream()
            .filter(v -> doesSuppressionAffectFinding(finding, v)).findFirst();
    }

    public List<Analysis> process(Project project, ModuleConfig moduleConfig) throws DependencyTrackException {
        List<Finding> findings = findingsAction.getFindings(project, true);
        return findings.stream()
            .map(finding -> {
                Optional<VulnerabilitySuppression> vulnerabilitySuppression =
                    getVulnerabilitySuppression(finding, moduleConfig.getVulnerabilitySuppressions());
                if (vulnerabilitySuppression.isPresent() && !(finding.getAnalysis().getIsSuppressed())) {
                    // a suppression should be done but its currently missing
                    return new Analysis(
                        moduleConfig.getProjectUuid(),
                        finding.getComponent().getUuid(),
                        finding.getVulnerability().getUuid(),
                        vulnerabilitySuppression.get().getAnalysisDetails(),
                        vulnerabilitySuppression.get().getAnalysisState(),
                        vulnerabilitySuppression.get().getAnalysisJustification(),
                        vulnerabilitySuppression.get().getAnalysisResponse(),
                        true,
                        true
                    );
                } else if ((!(vulnerabilitySuppression.isPresent()) && finding.getAnalysis().getIsSuppressed())) {
                    // a finding is suppressed but it should not - so remove it
                    return new Analysis(
                        moduleConfig.getProjectUuid(),
                        finding.getComponent().getUuid(),
                        finding.getVulnerability().getUuid(),
                        "",
                        State.NOT_SET,
                        AnalysisJustification.NOT_SET,
                        AnalysisVendorResponse.NOT_SET,
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
