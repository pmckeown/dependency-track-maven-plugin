package io.github.pmckeown.dependencytrack.finding;

import static io.github.pmckeown.dependencytrack.finding.Analysis.State.FALSE_POSITIVE;
import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aDefaultFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.Severity.CRITICAL;
import static io.github.pmckeown.dependencytrack.finding.Severity.HIGH;
import static io.github.pmckeown.dependencytrack.finding.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.Severity.MEDIUM;
import static io.github.pmckeown.dependencytrack.finding.Severity.UNASSIGNED;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.github.pmckeown.util.Logger;
import java.util.List;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindingsAnalyserTest {

    @InjectMocks
    private FindingsAnalyser findingAnalyser;

    @Mock
    private Logger logger;

    @Test
    void thatWhenNoThresholdIsProvidedThePolicyCannotBeBreached() {
        boolean isPolicyBreached =
                findingAnalyser.doNumberOfFindingsBreachPolicy(aListOfFindings().build(), null);

        assertFalse(isPolicyBreached);
    }

    @Test
    void thatACriticalIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(CRITICAL)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(0, null, null, null, null));
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    void thatACriticalIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(CRITICAL)))
                .build();

        assertDoesNotThrow(
                () -> {
                    findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(2, 0, 0, 0, 0));
                },
                "No exception expected");
    }

    @Test
    void thatASuppressedCriticalIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(CRITICAL))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE)))
                .build();

        assertDoesNotThrow(
                () -> {
                    findingAnalyser.doNumberOfFindingsBreachPolicy(
                            findings, new FindingThresholds(0, null, null, null, null));
                },
                "No exception expected");
    }

    @Test
    void thatAHighIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(HIGH)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(null, 0, null, null, null));
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    void thatAHighIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(HIGH)))
                .build();

        assertDoesNotThrow(
                () -> {
                    findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(0, 2, 0, 0, 0));
                },
                "No exception expected");
    }

    @Test
    void thatASuppressedHighIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(HIGH))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE)))
                .build();

        assertDoesNotThrow(
                () -> {
                    findingAnalyser.doNumberOfFindingsBreachPolicy(
                            findings, new FindingThresholds(null, 0, null, null, null));
                },
                "No exception expected");
    }

    @Test
    void thatAMediumIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(MEDIUM)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(null, null, 0, null, null));
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    void thatAMediumIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(MEDIUM)))
                .build();

        assertDoesNotThrow(
                () -> {
                    findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(0, 0, 2, 0, 0));
                },
                "No exception expected");
    }

    @Test
    void thatASuppressedMediumIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(MEDIUM))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE)))
                .build();

        assertDoesNotThrow(
                () -> {
                    findingAnalyser.doNumberOfFindingsBreachPolicy(
                            findings, new FindingThresholds(null, null, 0, null, null));
                },
                "No exception expected");
    }

    @Test
    void thatALowIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(LOW)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(null, null, null, 0, null));
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    void thatALowIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(LOW)))
                .build();

        assertDoesNotThrow(
                () -> {
                    findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(0, 0, 0, 2, 0));
                },
                "No exception expected");
    }

    @Test
    void thatASuppressedLowIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(LOW))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE)))
                .build();

        assertDoesNotThrow(
                () -> {
                    findingAnalyser.doNumberOfFindingsBreachPolicy(
                            findings, new FindingThresholds(null, null, null, 0, null));
                },
                "No exception expected");
    }

    @Test
    void thatAUnassignedIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(UNASSIGNED)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(null, null, null, null, 0));
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    void thatAUnassignedIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(UNASSIGNED)))
                .build();

        assertDoesNotThrow(
                () -> {
                    findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(0, 0, 0, 0, 2));
                },
                "No exception expected");
    }

    @Test
    void thatASuppressedUnassignedIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(UNASSIGNED))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE)))
                .build();

        assertDoesNotThrow(
                () -> {
                    findingAnalyser.doNumberOfFindingsBreachPolicy(
                            findings, new FindingThresholds(null, null, null, null, 0));
                },
                "No exception expected");
    }
}
