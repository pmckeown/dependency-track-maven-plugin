package io.github.pmckeown.dependencytrack.policy;

import io.github.pmckeown.util.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policy.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationListBuilder.aListOfPolicyViolations;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PolicyAnalyserTest {

    @InjectMocks
    private PolicyAnalyser policyAnalyser;

    @Mock
    private Logger logger;

    @Test
    public void thatWhenNoConfigIsProvidedThePolicyViolationsCannotBeBreached() {
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(
                aListOfPolicyViolations().build(), null);
        assertFalse(isPolicyBreached);
    }

    @Test
    public void thatPolicyViolationCountGreaterThanTheDefinedThresholdWillLogWarning() {
        List<PolicyViolation> policyViolations = setupPolicyViolations();
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(policyViolations, new PolicyConfig(null, null, null, 1));
        verify(logger).warn("Number of policy violations [%d] exceeds the maximum allowed [%d]", 2, 1);
        assertTrue(isPolicyBreached);
    }

    @Test
    public void thatPolicyViolationCountLessThanTheDefinedThresholdWillReturnFalse() {
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(
                aListOfPolicyViolations().build(), new PolicyConfig(null, null, null, 1));
        assertFalse(isPolicyBreached);
    }

    @Test
    public void thatPolicyViolationNameMatchingOneWillLogWarning() {
        PolicyConfig policyConfig = new PolicyConfig();
        policyConfig.setPolicyName("testPolicy1");
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(setupPolicyViolations(), policyConfig);
        assertTrue(isPolicyBreached);
        verify(logger).warn("Policy [%s] is violated under component [%s] [%s]", "testPolicy1", "password-printer", "1.0.0");
    }

    @Test
    public void thatPolicyViolationNameMatchingMultipleWillLogWarning() {
        PolicyConfig policyConfig = new PolicyConfig();
        policyConfig.setPolicyName("testPolicy");
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(setupPolicyViolations(), policyConfig);
        assertTrue(isPolicyBreached);
        verify(logger).warn("Policy [%s] is violated under component [%s] [%s]", "testPolicy1", "password-printer", "1.0.0");
        verify(logger).warn("Policy [%s] is violated under component [%s] [%s]", "testPolicy2", "password-printer", "1.0.0");
    }

    public List<PolicyViolation> setupPolicyViolations() {
        return aListOfPolicyViolations()
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
    }
}
