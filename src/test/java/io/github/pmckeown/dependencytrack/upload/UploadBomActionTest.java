package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.*;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static io.github.pmckeown.dependencytrack.PollingConfig.TimeUnit.MILLIS;
import static io.github.pmckeown.dependencytrack.upload.UploadBomResponseBuilder.anUploadBomResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UploadBomActionTest {

    private static final String BOM_LOCATION = "target/test-classes/projects/run/bom.xml";

    @InjectMocks
    private UploadBomAction uploadBomAction;

    @Mock
    private BomEncoder bomEncoder;

    @Mock
    private BomClient bomClient;

    @Mock
    private CommonConfig commonConfig;

    @Spy
    private Poller<Boolean> poller = new Poller<>();

    @Mock
    private Logger logger;

    @Test
    public void thatWhenNoBomIsFoundThenFalseIsReturned() throws Exception {
        doReturn(Optional.empty()).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        boolean success = uploadBomAction.upload(BOM_LOCATION);

        assertThat(success, is(equalTo(false)));
    }

    @Test
    public void thatBomCanBeUploadedSuccessfully() throws Exception {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(anUploadBomSuccessResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class));
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        boolean success = uploadBomAction.upload(BOM_LOCATION);

        assertThat(success, is(equalTo(true)));
    }

    @Test
    public void thatBomUploadFailureReturnsFalse() {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(aNotFoundResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class));
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        try {
            uploadBomAction.upload(BOM_LOCATION);
            fail("DependencyTrackException expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatBomUploadExceptionResultsInException() {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doThrow(UnirestException.class).when(bomClient).uploadBom(any(UploadBomRequest.class));
        doReturn(PollingConfig.disabled()).when(commonConfig).getPollingConfig();

        try {
            uploadBomAction.upload(BOM_LOCATION);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatWhenPollingIsEnabledThatTheServerIsQueriedUntilBomIsFullyProcessed() throws Exception {
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

        uploadBomAction.upload(BOM_LOCATION);

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
        return new Response(404, "Not Found", false);
    }

}
