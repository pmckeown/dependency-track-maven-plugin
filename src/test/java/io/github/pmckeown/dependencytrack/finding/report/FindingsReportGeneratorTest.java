package io.github.pmckeown.dependencytrack.finding.report;

import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class FindingsReportGeneratorTest {

    @InjectMocks
    private FindingsReportGenerator findingsReportGenerator;

    @Mock
    private FindingsReportXmlReportWriter xmlReportWriter;

    @Mock
    private FindingsReportHtmlReportWriter htmlReportWriter;

    @Test
    public void thatBothReportsAreGenerated() throws Exception {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings().build();
        findingsReportGenerator.generate(null, findings, findingThresholds, false);
        verify(xmlReportWriter).write(isNull(), any(FindingsReport.class));
        verify(htmlReportWriter).write(isNull());
    }

    @Test
    public void thatExceptionWhenWritingXmlReportIsHandledAndHtmlIsNotAttempted() throws Exception {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings().build();

        doThrow(DependencyTrackException.class).when(xmlReportWriter).write(isNull(), any(FindingsReport.class));

        try {
            findingsReportGenerator.generate(null, findings, findingThresholds, false);
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verifyNoInteractions(htmlReportWriter);
    }

    @Test
    public void thatExceptionWhenWritingHtmlReportIsHandled() throws Exception {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings().build();

        doThrow(DependencyTrackException.class).when(htmlReportWriter).write(isNull());

        try {
            findingsReportGenerator.generate(null, findings, findingThresholds, false);
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verify(xmlReportWriter).write(isNull(), any(FindingsReport.class));
    }
}
