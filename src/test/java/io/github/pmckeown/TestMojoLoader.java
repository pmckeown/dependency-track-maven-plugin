package io.github.pmckeown;

import io.github.pmckeown.dependencytrack.finding.FindingsMojo;
import io.github.pmckeown.dependencytrack.metrics.MetricsMojo;
import io.github.pmckeown.dependencytrack.project.DeleteProjectMojo;
import io.github.pmckeown.dependencytrack.upload.UploadBomMojo;
import io.github.pmckeown.dependencytrack.score.ScoreMojo;
import org.apache.maven.plugin.testing.MojoRule;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Helper class to load Mojos
 *
 * @author Paul McKeown
 */
public final class TestMojoLoader {

    private static final String TEST_RESOURCES = "target/test-classes/projects/run/";

    private TestMojoLoader() {

    }

    public static UploadBomMojo loadUploadBomMojo(MojoRule mojoRule) throws Exception {
        UploadBomMojo uploadBomMojo = (UploadBomMojo) mojoRule.lookupConfiguredMojo(getPomFile(), "upload-bom");
        assertNotNull(uploadBomMojo);
        return uploadBomMojo;
    }

    public static ScoreMojo loadScoreMojo(MojoRule mojoRule) throws Exception {
        ScoreMojo scoreMojo = (ScoreMojo) mojoRule.lookupConfiguredMojo(getPomFile(), "score");
        assertNotNull(scoreMojo);
        return scoreMojo;
    }

    public static MetricsMojo loadMetricsMojo(MojoRule mojoRule) throws Exception {
        MetricsMojo metricsMojo = (MetricsMojo) mojoRule.lookupConfiguredMojo(getPomFile(), "metrics");
        assertNotNull(metricsMojo);
        return metricsMojo;
    }

    public static DeleteProjectMojo loadDeleteProjectMojo(MojoRule mojoRule) throws Exception {
        DeleteProjectMojo deleteProjectMojo = (DeleteProjectMojo) mojoRule.lookupConfiguredMojo(getPomFile(), "delete-project");
        assertNotNull(deleteProjectMojo);
        return deleteProjectMojo;
    }

    public static FindingsMojo loadFindingsMojo(MojoRule mojoRule) throws Exception {
        FindingsMojo findingsMojo = (FindingsMojo) mojoRule.lookupConfiguredMojo(getPomFile(), "findings");
        assertNotNull(findingsMojo);
        return findingsMojo;
    }

    private static File getPomFile() {
        File pom = new File(TEST_RESOURCES);
        assertNotNull(pom);
        assertTrue(pom.exists());
        return pom;
    }
}
