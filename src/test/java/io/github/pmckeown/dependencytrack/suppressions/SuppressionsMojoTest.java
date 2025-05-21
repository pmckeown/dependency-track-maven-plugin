package io.github.pmckeown.dependencytrack.suppressions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.finding.FindingListBuilder;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.dependencytrack.project.ProjectBuilder;
import io.github.pmckeown.util.Logger;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("unused")
@RunWith(MockitoJUnitRunner.class)
public class SuppressionsMojoTest {

    @InjectMocks
    private SuppressionsMojo suppressionsMojo;

    @Mock
    private SuppressionsAction suppressionsAction;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private ModuleConfig moduleConfig;

    @Mock
    private Logger logger;

    @Mock
    private FindingsProcessor findingsProcessor;

    @Mock
    private ProjectAction projectAction;

    @Mock
    private VulnerabilitySuppressionValidator vulnerabilitySuppressionValidator;

    @Test
    public void thatReportIsNotGeneratedWhenSkipIsTrue() throws Exception {
        suppressionsMojo.setSkip("true");

        suppressionsMojo.execute();

        verifyNoInteractions(suppressionsAction);
    }

    @Test
    public void thatSuppressionsArePosted() throws Exception {
        List<VulnerabilitySuppression> vulnerabilitySuppressions = new ArrayList<>();
        vulnerabilitySuppressions.add(VulnerabilitySuppressionBuilder.aVulnerabilitySuppression().build());
        vulnerabilitySuppressions.add(VulnerabilitySuppressionBuilder.fixType1VulnerabilitySuppression().build());
        vulnerabilitySuppressions.add(VulnerabilitySuppressionBuilder.fixType2VulnerabilitySuppression().build());

        doReturn(ProjectBuilder.aProject().build())
            .when(projectAction).getProject(any(ModuleConfig.class));
        doReturn(FindingListBuilder.aListOfFindings().build())
            .when(findingsProcessor).process(any(Project.class), any(ModuleConfig.class));

        suppressionsMojo.getSuppressions().setVulnerabilitySuppressions(vulnerabilitySuppressions);

        suppressionsMojo.execute();

        verify(vulnerabilitySuppressionValidator, times(3)).isInValidVulnerabilitySuppression(any(VulnerabilitySuppression.class));
        //noinspection unchecked
        verify(suppressionsAction).setProjectSuppressions(any(List.class));
    }


}
