package io.github.pmckeown.dependencytrack.finding;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;
import static io.github.pmckeown.dependencytrack.finding.Analysis.State.FALSE_POSITIVE;
import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.Severity.HIGH;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindingsPrinterTest {

    @InjectMocks
    private FindingsPrinter findingsPrinter;

    @Mock
    private Logger logger;

    @Test
    void thatWhenNoFindingsAreRetrievedThatIsLogged() {
        // Act
        Project project = aProject().withName("X").build();
        findingsPrinter.printFindings(project, null);

        // Assert
        verify(logger).info("No findings were retrieved for project: %s", "X");
    }

    @Test
    void thatWhenSomeFindingsAreRetrievedThatIsLogged() {
        // Act
        Project project = aProject().withName("X").build();
        List<Finding> findings = findingsList("whatever", true);
        findingsPrinter.printFindings(project, findings);

        // Assert
        verify(logger).info("%d finding(s) were retrieved for project: %s", 1, "X");
    }

    @Test
    void thatAnUnsuppressedSingleFindingIsPrintedCorrectly() {
        String descriptionPart = repeat("x", DELIMITER.length());
        String longDescription = repeat(descriptionPart, 4);
        Project project = aProject().withName("a").withVersion("1").build();
        List<Finding> findings = findingsList(longDescription, "CVE-2016-1", false);
        findingsPrinter.printFindings(project, findings);

        verify(logger).info("Printing findings for project %s-%s", "a", "1"); // Intro
        verify(logger).info(DELIMITER);
        verify(logger).info("%s (%s)", "CVE-2016-1", "NVD");
        verify(logger).info("%s: %s", "HIGH", "nz.co.dodgy:insecure-encrypter:20.0");
        verify(logger).info("");
        verify(logger, times(4)).info(descriptionPart);
    }

    @Test
    void thatASuppressedSingleFindingIsPrintedCorrectly() {
        Project project = aProject().withName("a").withVersion("1").build();
        List<Finding> findings = findingsList(null, true);
        findingsPrinter.printFindings(project, findings);

        verify(logger).info("Printing findings for project %s-%s", "a", "1");
        verify(logger).info(DELIMITER);
        verify(logger).info("%s: %s", "HIGH", "nz.co.dodgy:insecure-encrypter:20.0");
        verify(logger, times(2)).info("");
        verify(logger).info("Suppressed - %s", FALSE_POSITIVE.name());
    }

    /**
     * Regression test for issue: https://github.com/pmckeown/dependency-track-maven-plugin/issues/89
     */
    @Test
    void thatPercentCharactersInFindingsOutputAreEscapedForFormatting() {
        String findingContent = "crafted value that contains both ${} and %{} sequences, which causes";
        Project project = aProject().withName("a").withVersion("1").build();
        List<Finding> findings = findingsList(findingContent, false);

        findingsPrinter.printFindings(project, findings);

        verify(logger).info("crafted value that contains both ${} and %%{} sequences, which causes");
    }

    /**
     * Regression test for issue: https://github.com/pmckeown/dependency-track-maven-plugin/issues/89
     */
    @Test
    void thatNewLineCharactersInFindingsOutputAreRemovedForFormatting() {
        String findingContent = "be vulnerable.\n> \n> -- [redhat.com](https://bugzilla.redhat.com/show_bug";
        Project project = aProject().withName("a").withVersion("1").build();
        List<Finding> findings = findingsList(findingContent, false);

        findingsPrinter.printFindings(project, findings);

        verify(logger).info("be vulnerable.> > -- [redhat.com](https://bugzilla.redhat.com/show_bug");
    }

    /** Test for issue: https://github.com/pmckeown/dependency-track-maven-plugin/issues/281 */
    @Test
    void thatSanitisedContentPrintableWhenItShrinksAcrossAChunkBoundary() {
        int chunkSize = findingsPrinter.getPrintWidth();
        String findingContent = repeat("x", chunkSize - 1) + repeat("\n", 3) + repeat("y", chunkSize - 1);
        Project project = aProject().withName("a").withVersion("1").build();
        List<Finding> findings = findingsList(findingContent, false);

        findingsPrinter.printFindings(project, findings);

        verify(logger).info(repeat("x", chunkSize - 1) + "y");
        verify(logger).info(repeat("y", chunkSize - 2));
    }

    private List<Finding> findingsList(boolean isSuppressed, final VulnerabilityBuilder vulnerabilityBuilder) {
        return aListOfFindings()
                .withFinding(aFinding()
                        .withComponent(aComponent()
                                .withGroup("nz.co.dodgy")
                                .withName("insecure-encrypter")
                                .withVersion("20.0"))
                        .withVulnerability(vulnerabilityBuilder)
                        .withAnalysis(
                                anAnalysis().withSuppressed(isSuppressed).withState(Analysis.State.FALSE_POSITIVE)))
                .build();
    }

    private List<Finding> findingsList(String longDescription, String vulnId, boolean isSuppressed) {
        VulnerabilityBuilder vulnerabilityBuilder =
                aVulnerability().withSeverity(HIGH).withVulnId(vulnId).withDescription(longDescription);
        return findingsList(isSuppressed, vulnerabilityBuilder);
    }

    private List<Finding> findingsList(String longDescription, boolean isSuppressed) {
        return findingsList(longDescription, "CVE-2016-" + randomNumeric(4), isSuppressed);
    }
}
