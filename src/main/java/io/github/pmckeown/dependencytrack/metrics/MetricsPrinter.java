package io.github.pmckeown.dependencytrack.metrics;

import static io.github.pmckeown.dependencytrack.Constants.COMPONENTS;
import static io.github.pmckeown.dependencytrack.Constants.CRITICAL;
import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;
import static io.github.pmckeown.dependencytrack.Constants.FINDINGS_AUDITED;
import static io.github.pmckeown.dependencytrack.Constants.FINDINGS_TOTAL;
import static io.github.pmckeown.dependencytrack.Constants.FINDINGS_UNAUDITED;
import static io.github.pmckeown.dependencytrack.Constants.FIRST_OCCURRENCE;
import static io.github.pmckeown.dependencytrack.Constants.HIGH;
import static io.github.pmckeown.dependencytrack.Constants.INHERITED_RISK_SCORE;
import static io.github.pmckeown.dependencytrack.Constants.LAST_OCCURRENCE;
import static io.github.pmckeown.dependencytrack.Constants.LOW;
import static io.github.pmckeown.dependencytrack.Constants.MEDIUM;
import static io.github.pmckeown.dependencytrack.Constants.METRIC;
import static io.github.pmckeown.dependencytrack.Constants.SUPPRESSED;
import static io.github.pmckeown.dependencytrack.Constants.UNASSIGNED;
import static io.github.pmckeown.dependencytrack.Constants.VALUE;
import static io.github.pmckeown.dependencytrack.Constants.VULNERABILITIES;
import static io.github.pmckeown.dependencytrack.Constants.VULNERABLE_COMPONENTS;
import static org.apache.commons.lang3.StringUtils.leftPad;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Singleton
class MetricsPrinter {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsPrinter.class);

    void print(Metrics metrics) {
        if (!LOG.isInfoEnabled()) {
            return;
        }
        LOG.info(DELIMITER);
        LOG.info(formatForPrinting(METRIC, VALUE));
        LOG.info(DELIMITER);
        LOG.info(formatForPrinting(INHERITED_RISK_SCORE, metrics.getInheritedRiskScore()));
        LOG.info(formatForPrinting(CRITICAL, metrics.getCritical()));
        LOG.info(formatForPrinting(HIGH, metrics.getHigh()));
        LOG.info(formatForPrinting(MEDIUM, metrics.getMedium()));
        LOG.info(formatForPrinting(LOW, metrics.getLow()));
        LOG.info(formatForPrinting(UNASSIGNED, metrics.getUnassigned()));
        LOG.info(formatForPrinting(VULNERABILITIES, metrics.getVulnerabilities()));
        LOG.info(formatForPrinting(VULNERABLE_COMPONENTS, metrics.getVulnerableComponents()));
        LOG.info(formatForPrinting(COMPONENTS, metrics.getComponents()));
        LOG.info(formatForPrinting(SUPPRESSED, metrics.getSuppressed()));
        LOG.info(formatForPrinting(FINDINGS_TOTAL, metrics.getFindingsTotal()));
        LOG.info(formatForPrinting(FINDINGS_AUDITED, metrics.getFindingsAudited()));
        LOG.info(formatForPrinting(FINDINGS_UNAUDITED, metrics.getFindingsUnaudited()));
        LOG.info(formatForPrinting(FIRST_OCCURRENCE, formatDate(metrics.getFirstOccurrence())));
        LOG.info(formatForPrinting(LAST_OCCURRENCE, formatDate(metrics.getLastOccurrence())));
        LOG.info(DELIMITER);
    }

    private String formatForPrinting(String key, Object value) {
        return String.format("%s | %s", leftPad(key, 34), value);
    }

    private String formatDate(Date date) {
        ZonedDateTime zonedDateTime = date.toInstant().atZone(ZoneId.systemDefault());
        DateTimeFormatter isoDateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return zonedDateTime.format(isoDateFormatter);
    }
}
