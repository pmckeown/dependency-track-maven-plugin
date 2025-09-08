package io.github.pmckeown.dependencytrack.finding.report;

import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import io.github.pmckeown.dependencytrack.finding.Severity;
import java.util.List;
import org.junit.jupiter.api.Test;

public class FindingsReportTest {

    @Test
    public void thatAFindingsReportCanBeGenerated() {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings()
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability())
                        .withComponent(aComponent().withName("shonky-lib")))
                .build();
        FindingsReport findingsReport = new FindingsReport(findingThresholds, findings, true);

        assertThat(findingsReport.getCritical().getCount(), is(equalTo(1)));
        assertThat(
                findingsReport.getCritical().getFindings().get(0).getComponent().getName(), is(equalTo("shonky-lib")));
    }

    @Test
    public void thatAFindingsAreSortedIntoSeparateBuckets() {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings()
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability().withSeverity(Severity.CRITICAL))
                        .withComponent(aComponent()))
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability().withSeverity(Severity.HIGH))
                        .withComponent(aComponent()))
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability().withSeverity(Severity.MEDIUM))
                        .withComponent(aComponent()))
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability().withSeverity(Severity.LOW))
                        .withComponent(aComponent()))
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability().withSeverity(Severity.UNASSIGNED))
                        .withComponent(aComponent()))
                .build();
        FindingsReport findingsReport = new FindingsReport(findingThresholds, findings, true);

        assertThat(findingsReport.getCritical().getCount(), is(equalTo(1)));
        assertThat(findingsReport.getHigh().getCount(), is(equalTo(1)));
        assertThat(findingsReport.getMedium().getCount(), is(equalTo(1)));
        assertThat(findingsReport.getLow().getCount(), is(equalTo(1)));
        assertThat(findingsReport.getUnassigned().getCount(), is(equalTo(1)));
    }
}
