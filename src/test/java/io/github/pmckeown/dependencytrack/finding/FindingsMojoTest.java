package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.finding.report.FindingsReportGenerator;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FindingsMojoTest {

    @InjectMocks
    private FindingsMojo findingsMojo;

    @Mock
    private ProjectAction projectAction;

    @Mock
    private FindingsAction findingsAction;

    @SuppressWarnings("unused")
    @Mock
    private FindingsPrinter findingsPrinter;

    @Mock
    private FindingsAnalyser findingsAnalyser;

    @SuppressWarnings("unused")
    @Mock
    private CommonConfig commonConfig;

    @Mock
    private FindingsReportGenerator findingsReportGenerator;

    @Test
    public void thatReportIsAlwaysGeneratedEvenWhenNoFindingsArePresent() throws Exception {
        findingsMojo.performAction();

        verify(findingsReportGenerator, times(1)).generate(
                null, new ArrayList<>(), null, false);
    }

}