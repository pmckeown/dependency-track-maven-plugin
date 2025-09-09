package io.github.pmckeown.dependencytrack.upload;

import static io.github.pmckeown.dependencytrack.PollingConfig.TimeUnit.MILLIS;
import static io.github.pmckeown.dependencytrack.upload.UploadBomResponseBuilder.anUploadBomResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.Poller;
import io.github.pmckeown.dependencytrack.PollingConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
class UploadBomActionTest {

    private static final String BOM_LOCATION = "target/test-classes/projects/run/bom.xml";

    @InjectMocks
    private UploadBomAction uploadBomAction;

    @Mock
    private BomClient bomClient;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private ModuleConfig moduleConfig;

    @Spy
    private Poller<Boolean> poller = new Poller<>();

    @Mock
    private Logger logger;

    @Test
    void thatWhenNoBomIsFoundThenFalseIsReturned() throws Exception {
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        boolean success = uploadBomAction.upload(moduleConfig, false);

        assertThat(success, is(equalTo(false)));
    }

    @Test
    void thatBomCanBeUploadedSuccessfully() throws Exception {
        doReturn(BOM_LOCATION).when(moduleConfig).getBomLocation();
        doReturn(anUploadBomSuccessResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class), anyBoolean());
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        boolean success = uploadBomAction.upload(moduleConfig, false);

        assertThat(success, is(equalTo(true)));
    }

    @Test
    void thatBomUploadFailureReturnsFalse() {
        doReturn(BOM_LOCATION).when(moduleConfig).getBomLocation();
        doReturn(aNotFoundResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class), anyBoolean());
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        try {
            uploadBomAction.upload(moduleConfig, false);
            fail("DependencyTrackException expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    void thatBomUploadExceptionResultsInException() {
        doReturn(BOM_LOCATION).when(moduleConfig).getBomLocation();
        doThrow(RuntimeException.class).when(bomClient).uploadBom(any(), anyBoolean());
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        try {
            uploadBomAction.upload(moduleConfig, false);
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    void thatWhenPollingIsEnabledThatTheServerIsQueriedUntilBomIsFullyProcessed() throws Exception {
        doReturn(BOM_LOCATION).when(moduleConfig).getBomLocation();
        doReturn(anUploadBomSuccessResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class), anyBoolean());
        doReturn(new PollingConfig(true, 1, 3, MILLIS)).when(commonConfig).getPollingConfig();

        // Create a new candidate as the polling behaviour needs to change for this test
        UploadBomAction uploadBomAction = new UploadBomAction(bomClient, new Poller<Boolean>(), commonConfig, logger);

        doReturn(aBomProcessingResponse(true))
                .doReturn(aBomProcessingResponse(true))
                .doReturn(aBomProcessingResponse(false))
                .when(bomClient)
                .isBomBeingProcessed(anyString());

        uploadBomAction.upload(moduleConfig, false);

        verify(bomClient, times(3)).isBomBeingProcessed(anyString());
    }

    /*
     * Helper methods
     */

    private Response<BomProcessingResponse> aBomProcessingResponse(boolean processing) {
        return new Response<>(
                200,
                "OK",
                true,
                Optional.of(BomProcessingResponseBuilder.aBomProcessingResponse()
                        .withProcessing(processing)
                        .build()));
    }

    private Response<UploadBomResponse> anUploadBomSuccessResponse() {
        return new Response<>(200, "OK", true, Optional.of(anUploadBomResponse().build()));
    }

    private Response aNotFoundResponse() {
        return new Response(404, "Not Found", false, Optional.of("The parent component could not be found."));
    }
}
