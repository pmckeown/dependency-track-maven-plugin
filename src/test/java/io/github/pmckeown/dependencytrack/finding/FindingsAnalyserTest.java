package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.Analysis.State.FALSE_POSITIVE;
import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aDefaultFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.Vulnerability.Severity.CRITICAL;
import static io.github.pmckeown.dependencytrack.finding.Vulnerability.Severity.HIGH;
import static io.github.pmckeown.dependencytrack.finding.Vulnerability.Severity.LOW;
import static io.github.pmckeown.dependencytrack.finding.Vulnerability.Severity.MEDIUM;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class FindingsAnalyserTest {

    @InjectMocks
    private FindingsAnalyser findingAnalyser;

    @Mock
    private Logger logger;

    @Test
    public void thatACriticalIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding().withVulnerability(aVulnerability().withSeverity(CRITICAL))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds());
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    public void thatACriticalIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding().withVulnerability(aVulnerability().withSeverity(CRITICAL))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds(2, 0, 0, 0));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatASuppressedCriticalIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(CRITICAL))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds());
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatAHighIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding().withVulnerability(aVulnerability().withSeverity(HIGH))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds());
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    public void thatAHighIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding().withVulnerability(aVulnerability().withSeverity(HIGH))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds(0, 2, 0, 0));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatASuppressedHighIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(HIGH))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds());
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatAMediumIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding().withVulnerability(aVulnerability().withSeverity(MEDIUM))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds());
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    public void thatAMediumIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding().withVulnerability(aVulnerability().withSeverity(MEDIUM))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds(0, 0, 2, 0));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatASuppressedMediumIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(MEDIUM))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds());
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatALowIssueCountGreaterThanTheDefinedThresholdWillThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding().withVulnerability(aVulnerability().withSeverity(LOW))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds());
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(MojoFailureException.class)));
        }
    }

    @Test
    public void thatALowIssueCountLessThanTheDefinedThresholdWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding().withVulnerability(aVulnerability().withSeverity(LOW))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds(0, 0, 0, 2));
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }

    @Test
    public void thatASuppressedLowIssueCountWillNotThrowMojoFailureException() {
        List<Finding> findings = aListOfFindings().withFinding(
                aDefaultFinding()
                        .withVulnerability(aVulnerability().withSeverity(LOW))
                        .withAnalysis(anAnalysis().withSuppressed(true).withState(FALSE_POSITIVE))).build();

        try {
            findingAnalyser.analyse(findings, new FindingThresholds());
        } catch (Exception ex) {
            fail("No exception expected");
        }
    }
}
