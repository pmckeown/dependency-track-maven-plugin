package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FindingsReportGeneratorTest {

    @InjectMocks
    private FindingsReportGenerator findingsReportGenerator;

    @Mock
    private XmlReportWriter xmlReportWriter;

    @Mock
    private HtmlReportWriter htmlReportWriter;

    @Test
    public void thatBothReportsAreGenerated() throws Exception {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings().build();
        findingsReportGenerator.generate(findings, findingThresholds, false);

        verify(xmlReportWriter).write(any(FindingsReport.class));
        verify(htmlReportWriter).write();
    }

    @Test
    public void thatExceptionWhenWritingXmlReportIsHandledAndHtmlIsNotAttempted() throws Exception {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings().build();

        doThrow(DependencyTrackException.class).when(xmlReportWriter).write(any(FindingsReport.class));

        try {
            findingsReportGenerator.generate(findings, findingThresholds, false);
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verifyZeroInteractions(htmlReportWriter);
    }

    @Test
    public void thatExceptionWhenWritingHtmlReportIsHandled() throws Exception {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings().build();

        doThrow(DependencyTrackException.class).when(htmlReportWriter).write();

        try {
            findingsReportGenerator.generate(findings, findingThresholds, false);
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verify(xmlReportWriter).write(any(FindingsReport.class));
    }
}