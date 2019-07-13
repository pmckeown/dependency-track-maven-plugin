package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.dependencytrack.ResourceConstants;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.testing.SilentLog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.doReturn;

// TODO - refactor to remove wiremock from these tests
@RunWith(MockitoJUnitRunner.class)
public class UploadBomActionIntegrationTest extends AbstractDependencyTrackIntegrationTest {

    private static final String BOM_LOCATION = "target/test-classes/project-to-test/bom.xml";

    @InjectMocks
    private UploadBomAction uploadBomAction;

    @Mock
    private BomEncoder bomEncoder;

    private Logger logger = new Logger(new SilentLog());

    @Before
    public void setup() {
        doReturn(Optional.of("encoded-bom")).when(bomEncoder).encodeBom(BOM_LOCATION, logger);
    }

    @Test
    public void thatBomCanBeUploadedSuccessfully() throws Exception {
        stubFor(put(urlEqualTo(ResourceConstants.V1_BOM)).willReturn(ok()));

        uploadBomAction.upload(new UploadBomConfig(getCommonConfig(), BOM_LOCATION), logger);

        verify(exactly(1), putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM)));
    }
}
