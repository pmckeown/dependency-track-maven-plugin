package io.github.pmckeown.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;
import org.apache.maven.plugin.testing.SilentLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BomEncoderTest {

    private BomEncoder bomEncoder = new BomEncoder();

    private Logger logger = new Logger(new SilentLog());

    @Test
    public void thatABomFileCanBeBase64Encoded() {
        Optional<String> encodedBom = bomEncoder.encodeBom("target/test-classes/projects/run/bom.xml", logger);

        assertThat(encodedBom.isPresent(), is(true));
    }

    @Test
    public void thatNoExceptionOccursWhenBomIsMissing() {
        Optional<String> encodedBom = bomEncoder.encodeBom("invalid/location", logger);

        assertThat(encodedBom.isPresent(), is(false));
    }
}
