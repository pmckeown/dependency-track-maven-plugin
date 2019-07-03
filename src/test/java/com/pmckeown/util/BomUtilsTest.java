package com.pmckeown.util;

import com.pmckeown.TestLog;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

public class BomUtilsTest {

    private final TestLog testLog = new TestLog();

    @Test
    public void thatBomCanBeBase64Encoded() {
        BomUtils.getBase64EncodedBom("src/test/resources/project-to-test/bom.xml", testLog);
        assertThat(testLog.getLogs(), hasItem(containsString("<bom xmlns=\"http://cyclonedx.org/schema/bom/1.1\"")));
    }

    @Test
    public void thatMissingBomMessageInLogs() {
        BomUtils.getBase64EncodedBom("missing.file", testLog);
        assertThat(testLog.getLogs(), hasItem(containsString("Filename not found")));
    }
}
