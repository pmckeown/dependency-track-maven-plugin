package io.github.pmckeown.dependencytrack.finding;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;
import static org.apache.commons.lang3.StringUtils.joinWith;

import io.github.pmckeown.dependencytrack.project.Project;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Singleton
class FindingsPrinter {
    private static final Logger LOG = LoggerFactory.getLogger(FindingsPrinter.class);

    void printFindings(Project project, List<Finding> findings) {
        if (findings == null || findings.isEmpty()) {
            LOG.info("No findings were retrieved for project: {}", project.getName());
            return;
        }
        LOG.info("{} finding(s) were retrieved for project: {}", findings.size(), project.getName());
        LOG.info("Printing findings for project {}-{}", project.getName(), project.getVersion());
        findings.forEach(finding -> {
            Vulnerability vulnerability = finding.getVulnerability();
            LOG.info(DELIMITER);
            LOG.info("{} ({})", vulnerability.getVulnId(), vulnerability.getSource());
            LOG.info("{}: {}", vulnerability.getSeverity().name(), getComponentDetails(finding));
            LOG.info(""); // Spacer
            List<String> wrappedDescriptionParts = splitString(vulnerability.getDescription());
            if (wrappedDescriptionParts != null && !wrappedDescriptionParts.isEmpty()) {
                wrappedDescriptionParts.forEach(s -> LOG.info(s));
            }
            if (finding.getAnalysis().isSuppressed()) {
                LOG.info("");
                LOG.info("Suppressed - {}", finding.getAnalysis().getState().name());
            }
        });
    }

    int getPrintWidth() {
        // We wrap printed lines to match the delimiter string width
        return DELIMITER.length();
    }

    private List<String> splitString(final String string) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }

        String percentEscaped = Strings.CS.replace(string, "%", "%%");
        String cleaned = Strings.CS.replace(percentEscaped, "\n", "");
        int chunkSize = getPrintWidth();
        final int numberOfChunks = (cleaned.length() + chunkSize - 1) / chunkSize;
        return IntStream.range(0, numberOfChunks)
                .mapToObj(i -> cleaned.substring(i * chunkSize, Math.min((i + 1) * chunkSize, cleaned.length())))
                .toList();
    }

    private String getComponentDetails(Finding finding) {
        Component component = finding.getComponent();
        return joinWith(":", component.getGroup(), component.getName(), component.getVersion());
    }
}
