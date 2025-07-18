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

import io.github.pmckeown.util.Logger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class MetricsPrinter {

    private Logger logger;

    @Inject
    MetricsPrinter(Logger logger) {
        this.logger = logger;
    }

    void print(Metrics metrics) {
        logger.info(DELIMITER);
        logger.info(formatForPrinting(METRIC, VALUE));
        logger.info(DELIMITER);
        logger.info(formatForPrinting(INHERITED_RISK_SCORE, metrics.getInheritedRiskScore()));
        logger.info(formatForPrinting(CRITICAL, metrics.getCritical()));
        logger.info(formatForPrinting(HIGH, metrics.getHigh()));
        logger.info(formatForPrinting(MEDIUM, metrics.getMedium()));
        logger.info(formatForPrinting(LOW, metrics.getLow()));
        logger.info(formatForPrinting(UNASSIGNED, metrics.getUnassigned()));
        logger.info(formatForPrinting(VULNERABILITIES, metrics.getVulnerabilities()));
        logger.info(formatForPrinting(VULNERABLE_COMPONENTS, metrics.getVulnerableComponents()));
        logger.info(formatForPrinting(COMPONENTS, metrics.getComponents()));
        logger.info(formatForPrinting(SUPPRESSED, metrics.getSuppressed()));
        logger.info(formatForPrinting(FINDINGS_TOTAL, metrics.getFindingsTotal()));
        logger.info(formatForPrinting(FINDINGS_AUDITED, metrics.getFindingsAudited()));
        logger.info(formatForPrinting(FINDINGS_UNAUDITED, metrics.getFindingsUnaudited()));
        logger.info(formatForPrinting(FIRST_OCCURRENCE, formatDate(metrics.getFirstOccurrence())));
        logger.info(formatForPrinting(LAST_OCCURRENCE, formatDate(metrics.getFirstOccurrence())));
        logger.info(DELIMITER);
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
