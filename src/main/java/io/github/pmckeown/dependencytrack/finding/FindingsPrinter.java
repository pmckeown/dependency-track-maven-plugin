package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.joinWith;

@Singleton
class FindingsPrinter {

    private Logger logger;

    @Inject
    public FindingsPrinter(Logger logger) {
        this.logger = logger;
    }

    void printFindings(Project project, List<Finding> findings) {
        if (findings == null || findings.isEmpty()) {
            return;
        }

        logger.info("Printing findings for project %s-%s", project.getName(), project.getVersion());
        for (Finding finding : findings) {
            logger.info("========== Vulnerable component ==========");
            logger.info("Component: %s", getComponentDetails(finding));
            logger.info("Vulnerability: %s", getVulnerabilityDetails(finding));
            logger.info("Analysis: %s", getAnalysis(finding));
        }
    }

    private String getComponentDetails(Finding finding) {
        Component component = finding.getComponent();
        return joinWith(":", component.getGroup(), component.getName(), component.getVersion());
    }

    private String getVulnerabilityDetails(Finding finding) {
        Vulnerability vulnerability = finding.getVulnerability();
        return joinWith(" - ", vulnerability.getSeverity(), vulnerability.getSource(),
                vulnerability.getVulnId(), vulnerability.getDescription());
    }

    private String getAnalysis(Finding finding) {
        Analysis analysis = finding.getAnalysis();
        return joinWith(" - ", analysis.isSuppressed(),
                analysis.getState() != null ? analysis.getState().name() : null);
    }
}
