package io.github.pmckeown.dependencytrack.finding.report;

import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.pmckeown.dependencytrack.finding.Analysis;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import io.github.pmckeown.dependencytrack.finding.Severity;
import io.github.pmckeown.dependencytrack.report.TransformerFactoryProvider;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FindingsReportIntegrationTest {

    private FindingsReportXmlReportWriter xmlReportWriter;
    private FindingsReportHtmlReportWriter htmlReportWriter;

    @BeforeEach
    public void setUp() {
        FindingsReportMarshallerService findingsReportMarshallerService = new FindingsReportMarshallerService();
        xmlReportWriter = new FindingsReportXmlReportWriter(findingsReportMarshallerService);
        htmlReportWriter = new FindingsReportHtmlReportWriter(new TransformerFactoryProvider());
    }

    @Test
    public void thatXmlFileCanBeGenerated() {
        try {
            File outputDirectory = new File("target");
            xmlReportWriter.write(outputDirectory, new FindingsReport(thresholds(), findings(), true));
            assertThat(new File(outputDirectory, FindingsReportConstants.XML_REPORT_FILENAME).exists(), is(true));
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }

    @Test
    public void thatXmlFileCanBeTransformed() {
        try {
            File outputDirectory = new File("target");
            xmlReportWriter.write(outputDirectory, new FindingsReport(thresholds(), findings(), true));
            htmlReportWriter.write(outputDirectory);
            assertThat(new File(outputDirectory, FindingsReportConstants.HTML_REPORT_FILENAME).exists(), is(true));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Exception not expected");
        }
    }

    private FindingThresholds thresholds() {
        return new FindingThresholds(1, 2, 3, 4, null);
    }

    private List<Finding> findings() {
        return aListOfFindings()
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.CRITICAL))
                        .withComponent(aComponent())
                        .withAnalysis(anAnalysis()))
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.CRITICAL))
                        .withComponent(aComponent().withName("suppressed"))
                        .withAnalysis(anAnalysis()
                                .withState(Analysis.State.FALSE_POSITIVE)
                                .withSuppressed(true)))
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.HIGH))
                        .withComponent(aComponent())
                        .withAnalysis(anAnalysis()))
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.MEDIUM))
                        .withComponent(aComponent())
                        .withAnalysis(anAnalysis()))
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.LOW))
                        .withComponent(aComponent())
                        .withAnalysis(anAnalysis()))
                .withFinding(aFinding()
                        .withVulnerability(aVulnerability().withSeverity(Severity.UNASSIGNED))
                        .withComponent(aComponent())
                        .withAnalysis(anAnalysis()))
                .build();
    }
}
