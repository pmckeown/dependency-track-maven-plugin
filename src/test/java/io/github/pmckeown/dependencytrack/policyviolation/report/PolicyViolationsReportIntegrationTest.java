package io.github.pmckeown.dependencytrack.policyviolation.report;

import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationListBuilder.aListOfPolicyViolations;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.pmckeown.dependencytrack.policyviolation.Policy;
import io.github.pmckeown.dependencytrack.policyviolation.PolicyViolation;
import io.github.pmckeown.dependencytrack.policyviolation.ViolationState;
import io.github.pmckeown.dependencytrack.report.TransformerFactoryProvider;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PolicyViolationsReportIntegrationTest {

    private PolicyViolationsXmlReportWriter xmlReportWriter;
    private PolicyViolationsHtmlReportWriter htmlReportWriter;

    @BeforeEach
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
            assertThat(
                    new File(outputDirectory, PolicyViolationsReportConstants.XML_REPORT_FILENAME).exists(), is(true));
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }

    @Disabled("Until XSL Stylesheet is created")
    @Test
    public void thatXmlFileCanBeTransformed() {
        try {
            File outputDirectory = new File("target");
            xmlReportWriter.write(outputDirectory, new PolicyViolationsReport(policyViolations()));
            htmlReportWriter.write(outputDirectory);
            assertThat(
                    new File(outputDirectory, PolicyViolationsReportConstants.HTML_REPORT_FILENAME).exists(), is(true));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Exception not expected");
        }
    }

    private List<PolicyViolation> policyViolations() {
        List<PolicyViolation> policyViolations = aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(
                                aPolicyCondition().withPolicy(new Policy("testPolicy1", ViolationState.INFO)))
                        .withComponent(aComponent()))
                .build();
        return policyViolations;
    }
}
