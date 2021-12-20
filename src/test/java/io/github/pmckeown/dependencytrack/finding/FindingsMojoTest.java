package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.finding.report.FindingsReportGenerator;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SuppressWarnings("unused")
@RunWith(MockitoJUnitRunner.class)
public class FindingsMojoTest {

    @InjectMocks
    private FindingsMojo findingsMojo;

    @Mock
    private ProjectAction projectAction;

    @Mock
    private FindingsAction findingsAction;

    @Mock
    private FindingsPrinter findingsPrinter;

    @Mock
    private FindingsAnalyser findingsAnalyser;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private FindingsReportGenerator findingsReportGenerator;

    @Mock
    private Logger logger;

    @Test
    public void thatReportIsAlwaysGeneratedEvenWhenNoFindingsArePresent() throws Exception {
        findingsMojo.performAction();

        verify(findingsReportGenerator, times(1)).generate(
                null, new ArrayList<>(), null, false);
    }

    @Test
    public void thatReportIsNotGeneratedWhenSkipIsTrue() throws Exception {
        findingsMojo.setSkip(true);

        findingsMojo.execute();

        verifyNoInteractions(findingsReportGenerator);
    }

}