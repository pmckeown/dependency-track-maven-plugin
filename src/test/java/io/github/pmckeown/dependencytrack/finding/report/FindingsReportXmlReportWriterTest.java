package io.github.pmckeown.dependencytrack.finding.report;

import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import java.io.File;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FindingsReportXmlReportWriterTest {

    @InjectMocks
    private FindingsReportXmlReportWriter xmlReportWriter;

    @Mock
    private FindingsReportMarshallerService marshallerService;

    @Mock
    private Marshaller marshaller;

    @Before
    public void setUp() throws Exception {
        doReturn(marshaller).when(marshallerService).getMarshaller();
    }

    @Test
    public void thatFindingReportCanBeMarshalled() {
        try {
            xmlReportWriter.write(null, new FindingsReport(someFindingThresholds(), someFindings(), true));
        } catch (Exception ex) {
            fail(format("No exception expected but got: %s", ex.getMessage()));
        }
    }

    @Test
    public void thatAnExceptionIsThrownWhenMarshallingFails() throws Exception {
        doThrow(JAXBException.class).when(marshaller).marshal(any(FindingsReport.class), any(File.class));
        try {
            xmlReportWriter.write(null, new FindingsReport(someFindingThresholds(), someFindings(), true));
            fail("Exception expected but none occurred");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(DependencyTrackException.class));
        }
    }

    private FindingThresholds someFindingThresholds() {
        return new FindingThresholds(1, 2, 3, 4, 5);
    }

    private List<Finding> someFindings() {
        List<Finding> findings = aListOfFindings()
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability())
                        .withComponent(aComponent()))
                .build();
        return findings;
    }
}
