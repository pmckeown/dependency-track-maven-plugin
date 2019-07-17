package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;
import kong.unirest.UnirestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class UploadBomActionTest {

    private static final String BOM_LOCATION = "target/test-classes/project-to-test/bom.xml";

    @InjectMocks
    private UploadBomAction uploadBomAction;

    @Mock
    private BomEncoder bomEncoder;

    @Mock
    private UploadBomClient uploadBomClient;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private Logger logger;

    @Test
    public void thatWhenNoBomIsFoundThenFalseIsReturned() throws Exception {
        doReturn(Optional.empty()).when(bomEncoder).encodeBom(BOM_LOCATION, logger);

        boolean success = uploadBomAction.upload(BOM_LOCATION);

        assertThat(success, is(equalTo(false)));
    }

    @Test
    public void thatBomCanBeUploadedSuccessfully() throws Exception {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(new Response(200, "OK", true)).when(uploadBomClient).uploadBom(any(Bom.class));

        boolean success = uploadBomAction.upload(BOM_LOCATION);

        assertThat(success, is(equalTo(true)));
    }

    @Test
    public void thatBomUploadFailureReturnsFalse() throws Exception {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doReturn(new Response(404, "Not Found", false)).when(uploadBomClient).uploadBom(any(Bom.class));

        boolean success = uploadBomAction.upload(BOM_LOCATION);

        assertThat(success, is(equalTo(false)));
    }

    @Test
    public void thatBomUploadExceptionResultsInException() throws Exception {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
        doThrow(UnirestException.class).when(uploadBomClient).uploadBom(any(Bom.class));

        try {
            uploadBomAction.upload(BOM_LOCATION);
        } catch (Exception ex) {
            assertThat(ex, is(instanceOf(DependencyTrackException.class)));
        }
    }

}
