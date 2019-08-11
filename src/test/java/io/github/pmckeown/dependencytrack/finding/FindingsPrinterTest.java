package io.github.pmckeown.dependencytrack.finding;

import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aDefaultFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FindingsPrinterTest {

    @InjectMocks
    private FindingsPrinter findingsPrinter;

    @Mock
    private Logger logger;

    @Test
    public void thatASingleFindingIsPrinted() {
        Project project = aProject().build();
        List<Finding> findings = aListOfFindings()
                .withFinding(aDefaultFinding()).build();
        findingsPrinter.printFindings(project, findings);

        verify(logger, times(1)).info(anyString(), anyString(), anyString());
        verify(logger, times(1)).info(anyString());
        verify(logger, times(3)).info(anyString(), anyString());
    }

}
