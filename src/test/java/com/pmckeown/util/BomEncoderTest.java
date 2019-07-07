package com.pmckeown.util;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test bom encoder
 *
 * @author Paul McKeown
 */
public class BomEncoderTest {

    private BomEncoder bomEncoder = new BomEncoder();

    @Test
    public void thatABomFileCanBeBase64Encoded() {
        Optional<String> encodedBom = bomEncoder.encodeBom("target/test-classes/project-to-test/bom.xml");

        assertThat(encodedBom.isPresent(), is(true));
    }

    @Test
    public void thatNoExceptionOccursWhenBomIsMissing() {
        Optional<String> encodedBom = bomEncoder.encodeBom("invalid/location");

        assertThat(encodedBom.isPresent(), is(false));
    }
}
