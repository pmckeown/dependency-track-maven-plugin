package io.github.pmckeown.dependencytrack;

import java.io.InputStream;
import java.util.Properties;

public final class Constants {

    public static final String VERSION;

    public static final String DELIMITER = "========================================================================";

    public static final String FINDINGS_UNAUDITED = "Findings Unaudited";
    public static final String INHERITED_RISK_SCORE = "Inherited Risk Score";
    public static final String METRIC = "Metric";
    public static final String VALUE = "Value";
    public static final String CRITICAL = "Critical";
    public static final String HIGH = "High";
    public static final String MEDIUM = "Medium";
    public static final String LOW = "Low";
    public static final String UNASSIGNED = "Unassigned";
    public static final String VULNERABILITIES = "Vulnerabilities";
    public static final String VULNERABLE_COMPONENTS = "Vulnerable Components";
    public static final String COMPONENTS = "Components";
    public static final String SUPPRESSED = "Suppressed";
    public static final String FINDINGS_TOTAL = "Findings Total";
    public static final String FINDINGS_AUDITED = "Findings Audited";
    public static final String FIRST_OCCURRENCE = "First Occurrence";
    public static final String LAST_OCCURRENCE = "Last Occurrence";

    static {
        String version = "unknown";
        try (InputStream is = Constants.class.getResourceAsStream(
                "/META-INF/maven/io.github.pmckeown/dependency-track-maven-plugin/pom.properties")) {
            if (is != null) {
                Properties properties = new Properties();
                properties.load(is);
                version = properties.getProperty("version", version);
            }
        } catch (Exception e) {
            // ignore
        }
        VERSION = version;
    }

    private Constants() {
        // Hiding implicit constructor
    }
}
