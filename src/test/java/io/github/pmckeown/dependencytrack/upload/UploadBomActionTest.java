package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.builders.BomProcessingResponseBuilder;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static io.github.pmckeown.dependencytrack.builders.UploadBomResponseBuilder.anUploadBomResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UploadBomActionTest {

    private static final String BOM_LOCATION = "target/test-classes/project-to-test/bom.xml";

    @InjectMocks
    private UploadBomAction uploadBomAction;

    @Mock
    private BomEncoder bomEncoder;

    @Mock
    private BomClient bomClient;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private Logger logger;

    @Mock
    private Sleeper sleeper;

    @Test
    public void thatWhenNoBomIsFoundThenFalseIsReturned() throws Exception {
        doReturn(Optional.empty()).when(bomEncoder).encodeBom(BOM_LOCATION, logger);

        boolean success = uploadBomAction.upload(BOM_LOCATION, false);

        assertThat(success, is(equalTo(false)));
    }

    @Test
    public void thatBomCanBeUploadedSuccessfully() throws Exception {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(anUploadBomSuccessResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class));

        boolean success = uploadBomAction.upload(BOM_LOCATION, false);

        assertThat(success, is(equalTo(true)));
    }

    @Test
    public void thatBomUploadFailureReturnsFalse() throws Exception {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(aNotFoundResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class));

        try {
            uploadBomAction.upload(BOM_LOCATION, false);
            fail("DependencyTrackException expected");
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatBomUploadExceptionResultsInException() throws Exception {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doThrow(UnirestException.class).when(bomClient).uploadBom(any(UploadBomRequest.class));

        try {
            uploadBomAction.upload(BOM_LOCATION, false);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

    @Test
    public void thatWhenPollingIsEnabledThatTheServerIsQueriedUntilBomIsFullyProcessed() throws Exception {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(anUploadBomSuccessResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class));
        doReturn(aBomProcessingResponse(true))
                .doReturn(aBomProcessingResponse(true))
                .doReturn(aBomProcessingResponse(false))
                .when(bomClient).isBomBeingProcessed(anyString());

        uploadBomAction.upload(BOM_LOCATION, true);

        verify(bomClient, times(3)).isBomBeingProcessed(anyString());
    }

    @Test
    public void thatSleepInterruptionCausesPluginToContinue() throws Exception {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(anUploadBomSuccessResponse()).when(bomClient).uploadBom(any(UploadBomRequest.class));
        doReturn(aBomProcessingResponse(true)).when(bomClient).isBomBeingProcessed(anyString());
        doThrow(new InterruptedException("Wakey wakey")).when(sleeper).sleep(anyInt());

        try {
            uploadBomAction.upload(BOM_LOCATION, true);
        } catch (DependencyTrackException ex) {
            fail("No exception expected");
        }

        verify(bomClient, times(1)).uploadBom(any(UploadBomRequest.class));
        verify(bomClient, times(1)).isBomBeingProcessed(anyString());
    }

    /*
     * Helper methods
     */

    private Response<BomProcessingResponse> aBomProcessingResponse(boolean processing) {
        return new Response<BomProcessingResponse>(200, "OK", true,
                Optional.of(BomProcessingResponseBuilder.aBomProcessingResponse().withProcessing(processing).build()));
    }

    private Response<UploadBomResponse> anUploadBomSuccessResponse() {
        return new Response<UploadBomResponse>(200, "OK", true,
                Optional.of(anUploadBomResponse().build()));
    }

    private Response aNotFoundResponse() {
        return new Response(404, "Not Found", false);
    }

}
