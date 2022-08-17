package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.finding.Analysis;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import io.github.pmckeown.dependencytrack.finding.Severity;
import io.github.pmckeown.dependencytrack.policy.Policy;
import io.github.pmckeown.dependencytrack.policy.PolicyViolation;
import io.github.pmckeown.dependencytrack.report.TransformerFactoryProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static io.github.pmckeown.dependencytrack.policy.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationListBuilder.aListOfPolicyViolations;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class PolicyViolationsReportIntegrationTest {

    private PolicyViolationsXmlReportWriter xmlReportWriter;
    private PolicyViolationsHtmlReportWriter htmlReportWriter;

    @Before
    public void setUp() {
        PolicyViolationsReportMarshallerService PolicyViolationsMarshallerService =
                new PolicyViolationsReportMarshallerService();
        xmlReportWriter = new PolicyViolationsXmlReportWriter(PolicyViolationsMarshallerService);
        htmlReportWriter = new PolicyViolationsHtmlReportWriter(new TransformerFactoryProvider());
    }

    @Test
    public void thatXmlFileCanBeGenerated() {
        try {
            File outputDirectory = new File("target");
            xmlReportWriter.write(outputDirectory, new PolicyViolationsReport(policyViolations()));
            assertThat(new File(outputDirectory, PolicyViolationsReportConstants.XML_REPORT_FILENAME).exists(),
                    is(true));
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }

    @Ignore("Until XSL Stylesheet is created")
    @Test
    public void thatXmlFileCanBeTransformed() {
        try {
            File outputDirectory = new File("target");
            xmlReportWriter.write(outputDirectory, new PolicyViolationsReport(policyViolations()));
            htmlReportWriter.write(outputDirectory);
            assertThat(new File(outputDirectory, PolicyViolationsReportConstants.HTML_REPORT_FILENAME).exists(),
                    is(true));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Exception not expected");
        }
    }

    private List<PolicyViolation> policyViolations() {
        List<PolicyViolation> policyViolations = aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy("testPolicy1", "INFO")))
                        .withComponent(aComponent())).build();
        return policyViolations;
    }

    private FindingThresholds thresholds() {
        return new FindingThresholds(1, 2, 3, 4, null);
    }

    private List<Finding> findings() {
        return aListOfFindings()
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.CRITICAL))
                        .withComponent(aComponent())
                        .withAnalysis(anAnalysis()))
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.CRITICAL))
                        .withComponent(aComponent().withName("suppressed"))
                        .withAnalysis(anAnalysis().withState(Analysis.State.FALSE_POSITIVE).withSuppressed(true)))
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.HIGH))
                        .withComponent(aComponent())
                        .withAnalysis(anAnalysis()))
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.MEDIUM))
                        .withComponent(aComponent())
                        .withAnalysis(anAnalysis()))
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.LOW))
                        .withComponent(aComponent())
                        .withAnalysis(anAnalysis()))
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.UNASSIGNED))
                        .withComponent(aComponent())
                        .withAnalysis(anAnalysis()))
                .build();
    }
}
