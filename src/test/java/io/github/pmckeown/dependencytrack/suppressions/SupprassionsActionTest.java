package io.github.pmckeown.dependencytrack.suppressions;

import static io.github.pmckeown.dependencytrack.PollingConfig.TimeUnit.MILLIS;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static io.github.pmckeown.dependencytrack.upload.UploadBomResponseBuilder.anUploadBomResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.Poller;
import io.github.pmckeown.dependencytrack.PollingConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.finding.AnalysisBuilder;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.upload.BomClient;
import io.github.pmckeown.dependencytrack.upload.BomProcessingResponse;
import io.github.pmckeown.dependencytrack.upload.BomProcessingResponseBuilder;
import io.github.pmckeown.dependencytrack.upload.UploadBomAction;
import io.github.pmckeown.dependencytrack.upload.UploadBomRequest;
import io.github.pmckeown.dependencytrack.upload.UploadBomResponse;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
    public void thatBomCanBeUploadedSuccessfully() throws Exception {
        doReturn(BOM_LOCATION).when(moduleConfig).getBomLocation();
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(anUploadBomSuccessResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class));
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        boolean success = uploadBomAction.upload(moduleConfig);

        assertThat(success, is(equalTo(true)));
    }

    @Test
    public void thatBomUploadFailureReturnsFalse() {
        doReturn(BOM_LOCATION).when(moduleConfig).getBomLocation();
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(aNotFoundResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class));
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        try {
            uploadBomAction.upload(moduleConfig);
            fail("DependencyTrackException expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatBomUploadExceptionResultsInException() {
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        try {
            uploadBomAction.upload(moduleConfig);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatWhenPollingIsEnabledThatTheServerIsQueriedUntilBomIsFullyProcessed() throws Exception {
        doReturn(BOM_LOCATION).when(moduleConfig).getBomLocation();
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(anUploadBomSuccessResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class));
        doReturn(new PollingConfig(true, 1, 3, MILLIS)).when(commonConfig).getPollingConfig();

        // Create a new candidate as the polling behaviour needs to change for this test
        UploadBomAction uploadBomAction = new UploadBomAction(bomClient, bomEncoder, new Poller<Boolean>(),
                commonConfig, logger);

        doReturn(aBomProcessingResponse(true))
                .doReturn(aBomProcessingResponse(true))
                .doReturn(aBomProcessingResponse(false))
                .when(bomClient).isBomBeingProcessed(anyString());

        uploadBomAction.upload(moduleConfig);

        verify(bomClient, times(3)).isBomBeingProcessed(anyString());
    }

    /*
     * Helper methods
     */

    private Response<BomProcessingResponse> aBomProcessingResponse(boolean processing) {
        return new Response<>(200, "OK", true,
                Optional.of(BomProcessingResponseBuilder.aBomProcessingResponse().withProcessing(processing).build()));
    }

    private Response<UploadBomResponse> anUploadBomSuccessResponse() {
        return new Response<>(200, "OK", true,
                Optional.of(anUploadBomResponse().build()));
    }

    private Response aNotFoundResponse() {
        return new Response(404, "Not Found", false, Optional.of("The parent component could not be found."));
    }

}
