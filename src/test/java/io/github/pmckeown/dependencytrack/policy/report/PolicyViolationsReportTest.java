package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.policy.Policy;
import io.github.pmckeown.dependencytrack.policy.PolicyViolation;
import org.junit.Test;

import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policy.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationListBuilder.aListOfPolicyViolations;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PolicyViolationsReportTest {

    @Test
    public void thatAPolicyViolationReportCanBeGenerated() {

        List<PolicyViolation> policyViolations = aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy("testPolicy1", "INFO")))
                        .withComponent(aComponent())).build();
        PolicyViolationsReport policyViolationReport = new PolicyViolationsReport(policyViolations);
        assertThat(policyViolationReport.getPolicyViolations().getPolicyViolations().get(0).getPolicyCondition().getPolicy().getName(),
                is(equalTo("testPolicy1")));
    }

    @Test
    public void thatAFindingsAreSortedIntoSeparateBuckets() {
        List<PolicyViolation> policyViolations = aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy("testPolicy1", "INFO")))
                        .withComponent(aComponent()))
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy("testPolicy2", "WARN")))
                        .withComponent(aComponent()))
                .build();
        PolicyViolationsReport policyViolationReport = new PolicyViolationsReport(policyViolations);
        assertThat(policyViolationReport.getPolicyViolations().getCount(), is(equalTo(2)));
    }

}