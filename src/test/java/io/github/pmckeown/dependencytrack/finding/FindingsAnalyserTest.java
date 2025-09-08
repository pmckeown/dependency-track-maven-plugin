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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.pmckeown.util.Logger;
import java.util.List;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class FindingsAnalyserTest {

    @InjectMocks
    private FindingsAnalyser findingAnalyser;

    @Mock
    private Logger logger;

    @Test
    public void thatWhenNoThresholdIsProvidedThePolicyCannotBeBreached() {
        boolean isPolicyBreached =
                findingAnalyser.doNumberOfFindingsBreachPolicy(aListOfFindings().build(), null);

        assertFalse(isPolicyBreached);
    }

    @Test
    public void thatACriticalIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
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
    public void thatACriticalIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(CRITICAL)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(2, 0, 0, 0, 0));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatASuppressedCriticalIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(CRITICAL))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(0, null, null, null, null));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatAHighIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
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
    public void thatAHighIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(HIGH)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(0, 2, 0, 0, 0));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatASuppressedHighIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(HIGH))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(null, 0, null, null, null));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatAMediumIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
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
    public void thatAMediumIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(MEDIUM)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(0, 0, 2, 0, 0));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatASuppressedMediumIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(MEDIUM))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(null, null, 0, null, null));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatALowIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
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
    public void thatALowIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(LOW)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(0, 0, 0, 2, 0));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatASuppressedLowIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(LOW))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(null, null, null, 0, null));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatAUnassignedIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
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
    public void thatAUnassignedIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aDefaultFinding().withVulnerability(aVulnerability().withSeverity(UNASSIGNED)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(0, 0, 0, 0, 2));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatASuppressedUnassignedIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(UNASSIGNED))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE)))
                .build();

        try {
            findingAnalyser.doNumberOfFindingsBreachPolicy(findings, new FindingThresholds(null, null, null, null, 0));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }
}
