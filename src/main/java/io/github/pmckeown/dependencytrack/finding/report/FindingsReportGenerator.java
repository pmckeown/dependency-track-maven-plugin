package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.io.File;
import java.util.List;

@Singleton
public class FindingsReportGenerator {

    private XmlReportWriter xmlReportWriter;
    private HtmlReportWriter htmlReportWriter;

    @Inject
    public FindingsReportGenerator(XmlReportWriter xmlReportWriter, HtmlReportWriter htmlReportWriter) {
        this.xmlReportWriter = xmlReportWriter;
        this.htmlReportWriter = htmlReportWriter;
    }

    public void generate(File buildDirectory, List<Finding> findings, FindingThresholds findingThresholds, 
            boolean policyBreached) throws DependencyTrackException {
        FindingsReport findingsReport = new FindingsReport(findingThresholds, findings, policyBreached);
        xmlReportWriter.write(buildDirectory, findingsReport);
        htmlReportWriter.write(buildDirectory);
    }
}
