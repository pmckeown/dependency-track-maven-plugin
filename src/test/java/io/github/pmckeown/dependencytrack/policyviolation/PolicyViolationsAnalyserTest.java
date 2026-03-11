package io.github.pmckeown.dependencytrack.policyviolation;

import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationListBuilder.aListOfPolicyViolations;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationsAnalyser.ERROR_TEMPLATE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.github.pmckeown.test.logging.SpyLoggerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class PolicyViolationsAnalyserTest {

    @InjectMocks
    private PolicyViolationsAnalyser policyAnalyser;

    @Test
    void thatInfoLevelPolicyViolationsWithFailOnWarnFalseDoesNotResultInPolicyBreach() {
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

        Logger logger = SpyLoggerRegistry.expectLogger(PolicyViolationsAnalyser.class);
        verify(logger).info(anyString());
        verify(logger, never()).warn(anyString());
    }

    @Test
    void thatInfoLevelPolicyViolationsWithFailOnWarnTrueDoesNotResultInPolicyBreach() {
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

        Logger logger = SpyLoggerRegistry.expectLogger(PolicyViolationsAnalyser.class);
        verify(logger).info(anyString());
        verify(logger, never()).warn(anyString());
    }

    @Test
    void thatWarnLevelPolicyViolationsWithFailOnWarnFalseDoesNotResultInPolicyBreach() {
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

        Logger logger = SpyLoggerRegistry.expectLogger(PolicyViolationsAnalyser.class);
        verify(logger).info(anyString());
        verify(logger, never()).warn(anyString());
    }

    @Test
    void thatWarnLevelPolicyViolationsWithFailOnWarnTrueDoesResultInPolicyBreach() {
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

        Logger logger = SpyLoggerRegistry.expectLogger(PolicyViolationsAnalyser.class);
        verify(logger).info(anyString());
        verify(logger).warn(ERROR_TEMPLATE, "Warn Severity Policy", "password-printer", "1.0.0");
    }

    @Test
    void thatFailLevelPolicyViolationsWithFailOnWarnFalseDoesResultInPolicyBreach() {
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

        Logger logger = SpyLoggerRegistry.expectLogger(PolicyViolationsAnalyser.class);
        verify(logger).info(anyString());
        verify(logger).warn(ERROR_TEMPLATE, "Fail Severity Policy", "password-printer", "1.0.0");
    }

    @Test
    void thatFailLevelPolicyViolationsWithFailOnWarnTrueDoesResultInPolicyBreach() {
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

        Logger logger = SpyLoggerRegistry.expectLogger(PolicyViolationsAnalyser.class);
        verify(logger).info(anyString());
        verify(logger).warn(ERROR_TEMPLATE, "Fail Severity Policy", "password-printer", "1.0.0");
    }
}
