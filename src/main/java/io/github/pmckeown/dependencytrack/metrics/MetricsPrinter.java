package io.github.pmckeown.dependencytrack.metrics;

import io.github.pmckeown.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;

@Singleton
class MetricsPrinter {

    private Logger logger;

    @Inject
    MetricsPrinter(Logger logger) {
        this.logger = logger;
    }

    void print(Metrics metrics) {
        logger.info(DELIMITER);
        logger.info("                Metric | Value");
        logger.info(DELIMITER);
        logger.info("  Inherited Risk Score | %s ", metrics.getInheritedRiskScore());
        logger.info("       Critical Issues | %s ", metrics.getCritical());
        logger.info("           High Issues | %s ", metrics.getHigh());
        logger.info("         Medium Issues | %s ", metrics.getMedium());
        logger.info("            Low Issues | %s ", metrics.getLow());
        logger.info("     Unassigned Issues | %s ", metrics.getUnassigned());
        logger.info("       Vulnerabilities | %s ", metrics.getVulnerabilities());
        logger.info(" Vulnerable Components | %s ", metrics.getVulnerableComponents());
        logger.info("            Components | %s ", metrics.getComponents());
        logger.info("            Suppressed | %s ", metrics.getSuppressed());
        logger.info("        Findings Total | %s ", metrics.getFindingsTotal());
        logger.info("      Findings Audited | %s ", metrics.getFindingsAudited());
        logger.info("    Findings Unaudited | %s ", metrics.getFindingsUnaudited());
//        logger.info(" First Occurrence      | %s ", formatDate(metrics.getFirstOccurrence()));
//        logger.info(" Last Occurrence       | %s ", formatDate(metrics.getLastOccurrence()));
        logger.info(DELIMITER);
    }

    // TODO - Better date handling
    private String formatDate(long timestamp) {
        LocalDateTime localDateTime = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter isoDateFormatter = DateTimeFormatter.ISO_DATE_TIME;
        return localDateTime.format(isoDateFormatter);
    }
}
