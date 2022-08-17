package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.policy.PolicyViolation;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;

@Singleton
public class PolicyViolationsReportGenerator {

    private PolicyViolationsXmlReportWriter xmlReportWriter;
    private PolicyViolationsHtmlReportWriter htmlReportWriter;

    @Inject
    public PolicyViolationsReportGenerator(PolicyViolationsXmlReportWriter xmlReportWriter,
            PolicyViolationsHtmlReportWriter htmlReportWriter) {
        this.xmlReportWriter = xmlReportWriter;
        this.htmlReportWriter = htmlReportWriter;
    }

    public void generate(File buildDirectory, List<PolicyViolation> policyViolations) throws DependencyTrackException {
        PolicyViolationsReport policyViolationReport = new PolicyViolationsReport(policyViolations);
        xmlReportWriter.write(buildDirectory, policyViolationReport);
        htmlReportWriter.write(buildDirectory);
    }
}
