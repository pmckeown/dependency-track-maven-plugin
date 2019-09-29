package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;
import static io.github.pmckeown.dependencytrack.finding.Analysis.State.FALSE_POSITIVE;
import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.Severity.HIGH;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FindingsPrinterTest {

    @InjectMocks
    private FindingsPrinter findingsPrinter;

    @Mock
    private Logger logger;

    @Test
    public void thatAUnsuppressedSingleFindingIsPrintedCorrectly() {
        String descriptionPart = repeat("x", DELIMITER.length());
        String longDescription = repeat(descriptionPart, 4);
        Project project = aProject().withName("a").withVersion("1").build();
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aFinding()
                                .withComponent(
                                        aComponent()
                                                .withGroup("nz.co.dodgy")
                                                .withName("insecure-encrypter")
                                                .withVersion("20.0"))
                                .withVulnerability(
                                        aVulnerability()
                                                .withSeverity(HIGH)
                                                .withDescription(longDescription))
                                .withAnalysis(
                                        anAnalysis()
                                                .withSuppressed(false))).build();
        findingsPrinter.printFindings(project, findings);

        verify(logger).info("Printing findings for project %s-%s", "a", "1"); // Intro
        verify(logger).info(DELIMITER);
        verify(logger).info("%s: %s", "HIGH", "nz.co.dodgy:insecure-encrypter:20.0");
        verify(logger).info("");
        verify(logger, times(4)).info(descriptionPart);
    }

    @Test
    public void thatASuppressedSingleFindingIsPrintedCorrectly() {
        Project project = aProject().withName("a").withVersion("1").build();
        List<Finding> findings = aListOfFindings()
                .withFinding(
                        aFinding()
                                .withComponent(
                                        aComponent()
                                                .withGroup("nz.co.dodgy")
                                                .withName("insecure-encrypter")
                                                .withVersion("20.0"))
                                .withVulnerability(
                                        aVulnerability()
                                                .withSeverity(HIGH)
                                                .withDescription(null))
                                .withAnalysis(
                                        anAnalysis()
                                                .withSuppressed(true)
                                                .withState(FALSE_POSITIVE))).build();
        findingsPrinter.printFindings(project, findings);

        verify(logger).info("Printing findings for project %s-%s", "a", "1");
        verify(logger).info(DELIMITER);
        verify(logger).info("%s: %s", "HIGH", "nz.co.dodgy:insecure-encrypter:20.0");
        verify(logger, times(2)).info("");
        verify(logger).info("Suppressed - %s", FALSE_POSITIVE.name());
    }

}
