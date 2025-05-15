package io.github.pmckeown.dependencytrack.suppressions;

import static io.github.pmckeown.dependencytrack.ResponseBuilder.aSuccessResponse;
import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static io.github.pmckeown.dependencytrack.suppressions.AnalysisBuilder.fixType1Analysis;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Optional;
import kong.unirest.UnirestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SupprassionsActionTest {

    @InjectMocks
    private SuppressionsAction suppressionsAction;

    @Mock
    AnalysisClient analysisClient;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private Logger logger;

    private Project project;

    private List<Analysis> analysisList;

    @Before
    public void setUp() {
        project = aProject().build();
    }

    @Test
    public void thatFindingsAreReturned() throws Exception {
        Project project = aProject().build();
        doReturn(aSuccessResponse().withBody(fixType1Analysis().build()).build())
            .when(analysisClient).uploadAnalysis(project.getUuid(), fixType1Analysis().build());

        Optional<UploadAnalysisResponse> returnedAnalysisResponse =
            analysisClient.uploadAnalysis(project.getUuid(), fixType1Analysis().build()).getBody();
        assertThat(returnedAnalysisResponse.isPresent(), is(true));
    }
}
