package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;
import static java.util.stream.Collectors.toList;
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
        findings.forEach(finding -> {
            Vulnerability vulnerability = finding.getVulnerability();
            logger.info(DELIMITER);
            logger.info("%s: %s", vulnerability.getSeverity().name(), getComponentDetails(finding));
            logger.info(""); // Spacer
            List<String> wrappedDescriptionParts = splitString(vulnerability.getDescription());
            if (wrappedDescriptionParts != null && !wrappedDescriptionParts.isEmpty()) {
                wrappedDescriptionParts.forEach(s -> logger.info(s));
            }
            if (finding.getAnalysis().isSuppressed()) {
                logger.info("");
                logger.info("Suppressed - %s", finding.getAnalysis().getState().name());
            }
        });
    }

    private List<String> splitString(final String string) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }

        int chunkSize = DELIMITER.length();
        final int numberOfChunks = (string.length() + chunkSize - 1) / chunkSize;
        return IntStream.range(0, numberOfChunks)
                .mapToObj(i -> string.substring(i * chunkSize, Math.min((i + 1) * chunkSize, string.length())))
                .collect(toList());
    }

    private String getComponentDetails(Finding finding) {
        Component component = finding.getComponent();
        return joinWith(":", component.getGroup(), component.getName(), component.getVersion());
    }

}
