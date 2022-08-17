package io.github.pmckeown.dependencytrack.policy.report;

class PolicyViolationsReportConstants {

    protected static final String XML_REPORT_FILENAME = "dependency-track-policy-violations.xml";

    protected static final String HTML_REPORT_FILENAME = "dependency-track-policy-violations.html";

    private PolicyViolationsReportConstants() {
        // Do no instantiate
    }

    static final String META_INF_DIRECTORY = "/META-INF";

    static final String XSL_STYLESHEET_FILENAME = META_INF_DIRECTORY +
            "/dependency-track-policy-violations-transformer.xsl";

}
