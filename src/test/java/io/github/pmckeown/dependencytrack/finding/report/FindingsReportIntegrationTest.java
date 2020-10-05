package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.finding.Analysis;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import io.github.pmckeown.dependencytrack.finding.Severity;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class FindingsReportIntegrationTest {

    private XmlReportWriter xmlReportWriter;
    private HtmlReportWriter htmlReportWriter;

    @Before
    public void setUp() {
        FindingsReportMarshallerService findingsReportMarshallerService = new FindingsReportMarshallerService();
        xmlReportWriter = new XmlReportWriter(findingsReportMarshallerService);
        htmlReportWriter = new HtmlReportWriter(new TransformerFactoryProvider());
    }

    @Test
    public void thatXmlFileCanBeGenerated() {
        try {
            xmlReportWriter.write(new FindingsReport(thresholds(), findings(), true));
            assertThat(new File(FindingsReportConstants.XML_REPORT_FILENAME).exists(), is(true));
        } catch (Exception ex) {
            fail("Exception not expected");
        }
    }

    @Test
    public void thatXmlFileCanBeTransformed() {
        try {
            xmlReportWriter.write(new FindingsReport(thresholds(), findings(), true));
            htmlReportWriter.write();
            assertThat(new File(FindingsReportConstants.HTML_REPORT_FILENAME).exists(), is(true));
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
                        .withAnalysis(anAnalysis().withState(Analysis.State.FALSE_POSITIVE).withSuppressed(true)))
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
