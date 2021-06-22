package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.finding.report.FindingsReportGenerator;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

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

    @Mock
    private FindingsPrinter findingsPrinter;

    @Mock
    private FindingsAnalyser findingsAnalyser;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private FindingsReportGenerator findingsReportGenerator;

    @Test
    public void thatReportIsAlwaysGenerated() throws Exception {
//        doReturn(ProjectBuilder.aProject().build()).when(projectAction).getProject(anyString(), anyString());
        List<Finding> findings = FindingListBuilder.aListOfFindings().build();
//        doReturn(findings).when(findingsAction).getFindings(any(Project.class));
//        doReturn(false).when(findingsAnalyser).doNumberOfFindingsBreachPolicy(
//                any(List.class), any(FindingThresholds.class));

        findingsMojo.performAction();

        verify(findingsReportGenerator, times(1)).generate(
                null, findings, null, false);
    }

}