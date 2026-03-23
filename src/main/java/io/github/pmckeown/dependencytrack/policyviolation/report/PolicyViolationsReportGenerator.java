package io.github.pmckeown.dependencytrack.policyviolation.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.policyviolation.PolicyViolation;
import java.io.File;
import java.util.List;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;

@Named
@Singleton
public class PolicyViolationsReportGenerator {

    private PolicyViolationsXmlReportWriter xmlReportWriter;

    @Inject
    public PolicyViolationsReportGenerator(PolicyViolationsXmlReportWriter xmlReportWriter) {
        this.xmlReportWriter = xmlReportWriter;
    }

    public void generate(File buildDirectory, List<PolicyViolation> policyViolations) throws DependencyTrackException {
        PolicyViolationsReport policyViolationReport = new PolicyViolationsReport(policyViolations);
        xmlReportWriter.write(buildDirectory, policyViolationReport);
    }
}
