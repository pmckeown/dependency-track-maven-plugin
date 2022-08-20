package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.policy.Policy;
import io.github.pmckeown.dependencytrack.policy.ViolationState;
import org.junit.Test;

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
        PolicyViolationsReport policyViolationReport = new PolicyViolationsReport(aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy("Info Policy", ViolationState.INFO)))
                        .withComponent(aComponent()))
                .withPolicyViolation(aPolicyViolation()
                        .withType("LICENSE")
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy("Warn Policy", ViolationState.WARN)))
                        .withComponent(aComponent())).build());

        assertThat(policyViolationReport.getPolicyViolations().getCount(), is(equalTo(2)));
        assertThat(policyViolationReport.getPolicyViolations().getPolicyViolations()
                        .get(0).getPolicyCondition().getPolicy().getName(),
                is(equalTo("Info Policy")));
    }

}