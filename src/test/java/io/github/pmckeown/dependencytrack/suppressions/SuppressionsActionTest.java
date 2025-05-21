package io.github.pmckeown.dependencytrack.suppressions;

import static io.github.pmckeown.dependencytrack.ResponseBuilder.aNotFoundResponse;
import static io.github.pmckeown.dependencytrack.ResponseBuilder.aSuccessResponse;
import static io.github.pmckeown.dependencytrack.suppressions.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.suppressions.AnalysisBuilder.fixType1Analysis;
import static io.github.pmckeown.dependencytrack.suppressions.AnalysisBuilder.fixType2Analysis;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.util.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("unused")
@RunWith(MockitoJUnitRunner.class)
public class SuppressionsActionTest {

    @InjectMocks
    private SuppressionsAction suppressionsAction;

    @Mock
    AnalysisClient analysisClient;

    @Mock
    private Logger logger;

    @Test
    public void thatPostingValidSuppressionReturnAResponse() {
        doReturn(aSuccessResponse().withBody(fixType1Analysis().build()).build())
            .when(analysisClient).uploadAnalysis(any(Analysis.class));

        Optional<UploadAnalysisResponse> returnedAnalysisResponse;
        try {
            returnedAnalysisResponse = suppressionsAction.doUpload(fixType1Analysis().build());
        } catch (DependencyTrackException e) {
            throw new RuntimeException(e);
        }

        assertThat(returnedAnalysisResponse.isPresent(), is(true));
    }

    @Test
    public void thatPostingInValidSuppressionResultsInException()  {
        doReturn(aNotFoundResponse().build()).when(analysisClient).uploadAnalysis(any(Analysis.class));

        try {
            suppressionsAction.doUpload(fixType1Analysis().build());
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatPostingValidSuppressionListIsSuccessful() {
        List<Analysis> analysisList = new ArrayList<>();
        analysisList.add(anAnalysis().build());
        analysisList.add(fixType1Analysis().build());
        analysisList.add(fixType2Analysis().build());

        doReturn(aSuccessResponse().withBody(anAnalysis().build()).build())
            .when(analysisClient).uploadAnalysis(any(Analysis.class));

        boolean result;
        try {
            result = suppressionsAction.setProjectSuppressions(analysisList);
        } catch (DependencyTrackException e) {
            throw new RuntimeException(e);
        }

        assertThat(result, is(true));
    }

    @Test
    public void thatPostingInvalidSuppressionListResultsInException() {
        List<Analysis> analysisList = new ArrayList<>();
        analysisList.add(anAnalysis().build());

        doReturn(aNotFoundResponse().build()).when(analysisClient).uploadAnalysis(any(Analysis.class));

        try {
            suppressionsAction.setProjectSuppressions(analysisList);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }
}
