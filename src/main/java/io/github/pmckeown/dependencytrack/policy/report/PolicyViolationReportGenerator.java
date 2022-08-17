package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.report.HtmlReportWriter;
import io.github.pmckeown.dependencytrack.finding.report.XmlReportWriter;
import io.github.pmckeown.dependencytrack.policy.PolicyViolation;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;

@Singleton
public class PolicyViolationReportGenerator {

    private XmlReportWriter xmlReportWriter;
    private HtmlReportWriter htmlReportWriter;

    @Inject
    public PolicyViolationReportGenerator(XmlReportWriter xmlReportWriter, HtmlReportWriter htmlReportWriter) {
        this.xmlReportWriter = xmlReportWriter;
        this.htmlReportWriter = htmlReportWriter;
    }

    public void generate(File buildDirectory, List<PolicyViolation> policyViolations) throws DependencyTrackException {
        PolicyViolationReport policyViolationReport = new PolicyViolationReport(policyViolations);
        xmlReportWriter.write(buildDirectory, policyViolationReport);
        htmlReportWriter.write(buildDirectory);
    }
}
