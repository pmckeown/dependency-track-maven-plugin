package io.github.pmckeown.dependencytrack;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.jupiter.api.BeforeEach;

@WireMockTest
public abstract class AbstractDependencyTrackMojoTest {
    static class MojoTestSupport extends AbstractMojoTestCase {
        @Override
        public void setUp() throws Exception {
            super.setUp();
        }
    }

    private static final String TEST_PROJECT = "target/test-classes/projects/run";

    private MojoTestSupport baseTest;
    private MojoRule rule;

    protected AbstractDependencyTrackMojoTest() {
        baseTest = new MojoTestSupport();
        rule = new MojoRule(baseTest);
    }

    @BeforeEach
    public void initTestCase() throws Exception {
        baseTest.setUp();
    }

    protected <T extends AbstractDependencyTrackMojo> T resolveMojo(String goal) throws Exception {
        T uploadBomMojo = (T) rule.lookupConfiguredMojo(getPomFile(), goal);
        uploadBomMojo.setPluginContext(getPluginContext());
        assertNotNull(uploadBomMojo);
        return uploadBomMojo;
    }

    private static Map<String, String> getPluginContext() {
        Map<String, String> map = new HashMap<>();
        map.put("groupId", "foo");
        map.put("artifactId", "bar");
        map.put("version", "1.2.3");
        map.put("packaging", "maven-plugin");
        return map;
    }

    private static File getPomFile() {
        File pom = new File(TEST_PROJECT);
        assertNotNull(pom);
        assertTrue(pom.exists());
        return pom;
    }
}
