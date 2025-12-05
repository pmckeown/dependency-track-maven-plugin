package io.github.pmckeown.dependencytrack.policyviolation.report;

import static io.github.pmckeown.dependencytrack.policyviolation.PolicyViolationListBuilder.aListOfPolicyViolations;
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
import io.github.pmckeown.dependencytrack.policyviolation.PolicyViolation;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PolicyViolationsReportGeneratorTest {

    @InjectMocks
    private PolicyViolationsReportGenerator policyViolationReportGenerator;

    @Mock
    private PolicyViolationsXmlReportWriter xmlReportWriter;

    @Mock
    private PolicyViolationsHtmlReportWriter htmlReportWriter;

    @Disabled("Until HTML report is generated")
    @Test
    void thatBothReportsAreGenerated() throws Exception {
        List<PolicyViolation> policyViolations = aListOfPolicyViolations().build();
        policyViolationReportGenerator.generate(null, policyViolations);

        verify(xmlReportWriter).write(isNull(), any(PolicyViolationsReport.class));
        verify(htmlReportWriter).write(isNull());
    }

    @Test
    void thatExceptionWhenWritingXmlReportIsHandledAndHtmlIsNotAttempted() throws Exception {

        doThrow(DependencyTrackException.class)
                .when(xmlReportWriter)
                .write(isNull(), any(PolicyViolationsReport.class));

        try {
            policyViolationReportGenerator.generate(null, new ArrayList<>());
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verifyNoInteractions(htmlReportWriter);
    }

    @Disabled("Until HTML report is generated")
    @Test
    void thatExceptionWhenWritingHtmlReportIsHandled() throws Exception {

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
