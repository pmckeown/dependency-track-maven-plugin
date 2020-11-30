package io.github.pmckeown.dependencytrack.finding.report;

class FindingsReportConstants {

    private FindingsReportConstants() {
        // Do no instantiate
    }

    protected static final String XML_REPORT_FILENAME = "dependency-track-findings.xml";

    protected static final String HTML_REPORT_FILENAME = "dependency-track-findings.html";

    static final String META_INF_DIRECTORY = "/META-INF";

    static final String XSL_STYLESHEET_FILENAME = META_INF_DIRECTORY + "/dependency-track-findings-transformer.xsl";

}
