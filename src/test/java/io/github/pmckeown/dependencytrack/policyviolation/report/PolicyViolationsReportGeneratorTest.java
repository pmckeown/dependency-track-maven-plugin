package io.github.pmckeown.dependencytrack.policyviolation.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.policyviolation.PolicyViolation;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationListBuilder.aListOfPolicyViolations;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class PolicyViolationsReportGeneratorTest {

    @InjectMocks
    private PolicyViolationsReportGenerator policyViolationReportGenerator;

    @Mock
    private PolicyViolationsXmlReportWriter xmlReportWriter;

    @Mock
    private PolicyViolationsHtmlReportWriter htmlReportWriter;

    @Ignore("Until HTML report is generated")
    @Test
    public void thatBothReportsAreGenerated() throws Exception {
        List<PolicyViolation> policyViolations = aListOfPolicyViolations().build();
        policyViolationReportGenerator.generate(null, policyViolations);

        verify(xmlReportWriter).write(isNull(), any(PolicyViolationsReport.class));
        verify(htmlReportWriter).write(isNull());
    }

    @Test
    public void thatExceptionWhenWritingXmlReportIsHandledAndHtmlIsNotAttempted() throws Exception {

        doThrow(DependencyTrackException.class).when(xmlReportWriter).write(isNull(), any(PolicyViolationsReport.class));

        try {
            policyViolationReportGenerator.generate(null, new ArrayList<>());
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verifyNoInteractions(htmlReportWriter);
    }

    @Ignore("Until HTML report is generated")
    @Test
    public void thatExceptionWhenWritingHtmlReportIsHandled() throws Exception {

        doThrow(DependencyTrackException.class).when(htmlReportWriter).write(isNull());

        try {
            policyViolationReportGenerator.generate(null, new ArrayList<>());
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verify(xmlReportWriter).write(isNull(), any(PolicyViolationsReport.class));
    }
}