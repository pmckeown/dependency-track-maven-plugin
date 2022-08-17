package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.report.HtmlReportWriter;
import io.github.pmckeown.dependencytrack.finding.report.XmlReportWriter;
import io.github.pmckeown.dependencytrack.policy.PolicyViolation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static io.github.pmckeown.dependencytrack.policy.PolicyViolationListBuilder.aListOfPolicyViolations;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class PolicyViolationReportGeneratorTest {

    @InjectMocks
    private PolicyViolationReportGenerator policyViolationReportGenerator;

    @Mock
    private XmlReportWriter xmlReportWriter;

    @Mock
    private HtmlReportWriter htmlReportWriter;

    @Test
    public void thatBothReportsAreGenerated() throws Exception {
        List<PolicyViolation> policyViolations = aListOfPolicyViolations().build();
        policyViolationReportGenerator.generate(null, policyViolations);

        verify(xmlReportWriter).write(isNull(), any(PolicyViolationReport.class));
        verify(htmlReportWriter).write(isNull());
    }

    @Test
    public void thatExceptionWhenWritingXmlReportIsHandledAndHtmlIsNotAttempted() throws Exception {

        doThrow(DependencyTrackException.class).when(xmlReportWriter).write(isNull(), any(PolicyViolationReport.class));

        try {
            policyViolationReportGenerator.generate(null, new ArrayList<>());
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verifyNoInteractions(htmlReportWriter);
    }

    @Test
    public void thatExceptionWhenWritingHtmlReportIsHandled() throws Exception {

        doThrow(DependencyTrackException.class).when(htmlReportWriter).write(isNull());

        try {
            policyViolationReportGenerator.generate(null, new ArrayList<>());
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verify(xmlReportWriter).write(isNull(), any(PolicyViolationReport.class));
    }
}