package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.policy.Policy;
import io.github.pmckeown.dependencytrack.policy.PolicyViolation;
import io.github.pmckeown.dependencytrack.policy.ViolationState;
import io.github.pmckeown.dependencytrack.report.TransformerFactoryProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
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
                                .withPolicy(new Policy("testPolicy1", ViolationState.INFO)))
                        .withComponent(aComponent())).build();
        return policyViolations;
    }

}
