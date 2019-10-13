package io.github.pmckeown.dependencytrack.finding.report;

class FindingsReportConstants {

    private FindingsReportConstants() {
        // Do no instantiate
    }

    static final String OUTPUT_DIRECTORY = "target";

    static final String META_INF_DIRECTORY = "/META-INF";

    static final String XSL_STYLESHEET_FILENAME = META_INF_DIRECTORY + "/dependency-track-findings-transformer.xsl";

    static final String XML_REPORT_FILENAME = OUTPUT_DIRECTORY + "/dependency-track-findings.xml";

    static final String HTML_REPORT_FILENAME = OUTPUT_DIRECTORY + "/dependency-track-findings.html";
}
