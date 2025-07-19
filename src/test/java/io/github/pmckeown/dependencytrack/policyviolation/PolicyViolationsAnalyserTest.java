package io.github.pmckeown.dependencytrack.policyviolation;

import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationListBuilder.aListOfPolicyViolations;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationsAnalyser.ERROR_TEMPLATE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import io.github.pmckeown.util.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PolicyViolationsAnalyserTest {

    @InjectMocks
    private PolicyViolationsAnalyser policyAnalyser;

    @Mock
    private Logger logger;

    @Test
    public void thatInfoLevelPolicyViolationsWithFailOnWarnFalseDoesNotResultInPolicyBreach() {
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(
                aListOfPolicyViolations()
                        .withPolicyViolation(aPolicyViolation()
                                .withType("SEVERITY")
                                .withPolicyCondition(aPolicyCondition()
                                        .withPolicy(new Policy("Info Severity Policy", ViolationState.INFO)))
                                .withComponent(aComponent()))
                        .build(),
                false);
        assertFalse(isPolicyBreached);

        verify(logger).info(anyString());
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void thatInfoLevelPolicyViolationsWithFailOnWarnTrueDoesNotResultInPolicyBreach() {
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(
                aListOfPolicyViolations()
                        .withPolicyViolation(aPolicyViolation()
                                .withType("SEVERITY")
                                .withPolicyCondition(aPolicyCondition()
                                        .withPolicy(new Policy("Info Severity Policy", ViolationState.INFO)))
                                .withComponent(aComponent()))
                        .build(),
                true);
        assertFalse(isPolicyBreached);

        verify(logger).info(anyString());
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void thatWarnLevelPolicyViolationsWithFailOnWarnFalseDoesNotResultInPolicyBreach() {
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(
                aListOfPolicyViolations()
                        .withPolicyViolation(aPolicyViolation()
                                .withType("SEVERITY")
                                .withPolicyCondition(aPolicyCondition()
                                        .withPolicy(new Policy("Warn Severity Policy", ViolationState.WARN)))
                                .withComponent(aComponent()))
                        .build(),
                false);
        assertFalse(isPolicyBreached);

        verify(logger).info(anyString());
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void thatWarnLevelPolicyViolationsWithFailOnWarnTrueDoesResultInPolicyBreach() {
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(
                aListOfPolicyViolations()
                        .withPolicyViolation(aPolicyViolation()
                                .withType("SEVERITY")
                                .withPolicyCondition(aPolicyCondition()
                                        .withPolicy(new Policy("Warn Severity Policy", ViolationState.WARN)))
                                .withComponent(aComponent()))
                        .build(),
                true);
        assertTrue(isPolicyBreached);

        verify(logger).info(anyString());
        verify(logger).warn(ERROR_TEMPLATE, "Warn Severity Policy", "password-printer", "1.0.0");
    }

    @Test
    public void thatFailLevelPolicyViolationsWithFailOnWarnFalseDoesResultInPolicyBreach() {
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(
                aListOfPolicyViolations()
                        .withPolicyViolation(aPolicyViolation()
                                .withType("SEVERITY")
                                .withPolicyCondition(aPolicyCondition()
                                        .withPolicy(new Policy("Fail Severity Policy", ViolationState.FAIL)))
                                .withComponent(aComponent()))
                        .build(),
                false);
        assertTrue(isPolicyBreached);

        verify(logger).info(anyString());
        verify(logger).warn(ERROR_TEMPLATE, "Fail Severity Policy", "password-printer", "1.0.0");
    }

    @Test
    public void thatFailLevelPolicyViolationsWithFailOnWarnTrueDoesResultInPolicyBreach() {
        boolean isPolicyBreached = policyAnalyser.isAnyPolicyViolationBreached(
                aListOfPolicyViolations()
                        .withPolicyViolation(aPolicyViolation()
                                .withType("SEVERITY")
                                .withPolicyCondition(aPolicyCondition()
                                        .withPolicy(new Policy("Fail Severity Policy", ViolationState.FAIL)))
                                .withComponent(aComponent()))
                        .build(),
                true);
        assertTrue(isPolicyBreached);

        verify(logger).info(anyString());
        verify(logger).warn(ERROR_TEMPLATE, "Fail Severity Policy", "password-printer", "1.0.0");
    }
}
