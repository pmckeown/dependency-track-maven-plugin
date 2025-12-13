package io.github.pmckeown.dependencytrack.upload;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import java.util.Collections;
import kong.unirest.Unirest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UploadBomMojoTest {

    private static final String PROJECT_NAME = "test";

    private static final String PROJECT_VERSION = "1.0";

    @InjectMocks
    private UploadBomMojo uploadBomMojo;

    @Mock
    private Log mavenLogger;

    @Mock
    private MavenProject project;

    @Mock
    private UploadBomAction uploadBomAction;

    @Mock
    private MetricsAction metricsAction;

    @Mock
    private ProjectAction projectAction;

    @Mock
    private Logger logger;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private ModuleConfig moduleConfig;

    @BeforeEach
    void setup() {
        uploadBomMojo.setLog(mavenLogger);
        uploadBomMojo.setCommonConfig(commonConfig);
        uploadBomMojo.setModuleConfig(moduleConfig);
        uploadBomMojo.setMavenProject(project);
    }

    @AfterEach
    void tearDown() {
        uploadBomMojo.getUnirestConfiguration().set(false);
    }

    @Test
    void thatTheUploadBomIsSkippedWhenSkipIsTrue() throws Exception {
        uploadBomMojo.setSkip("true");
        uploadBomMojo.setProjectName(PROJECT_NAME);
        uploadBomMojo.setProjectVersion(PROJECT_VERSION);

        uploadBomMojo.execute();

        verify(moduleConfig).setProjectName(PROJECT_NAME);
        verify(moduleConfig).setProjectVersion(PROJECT_VERSION);
        verifyNoInteractions(uploadBomAction);
        verifyNoInteractions(metricsAction);
        verifyNoInteractions(projectAction);
    }

    @Test
    void thatTheUploadBomIsSkippedWhenSkipIsReleases() throws Exception {
        uploadBomMojo.setSkip("releases");
        uploadBomMojo.setProjectName(PROJECT_NAME);
        uploadBomMojo.setProjectVersion(PROJECT_VERSION);

        uploadBomMojo.execute();

        verify(moduleConfig).setProjectName(PROJECT_NAME);
        verify(moduleConfig).setProjectVersion(PROJECT_VERSION);
        verifyNoInteractions(uploadBomAction);
        verifyNoInteractions(metricsAction);
        verifyNoInteractions(projectAction);
    }

    @Test
    void thatTheUploadBomIsSkippedWhenSkipIsSnapshots() throws Exception {
        String snapshotVersion = "1.0-SNAPSHOT";
        uploadBomMojo.setSkip("snapshots");
        uploadBomMojo.setProjectName(PROJECT_NAME);
        uploadBomMojo.setProjectVersion(snapshotVersion);

        uploadBomMojo.execute();

        verify(moduleConfig).setProjectName(PROJECT_NAME);
        verify(moduleConfig).setProjectVersion(snapshotVersion);
        verifyNoInteractions(uploadBomAction);
        verifyNoInteractions(metricsAction);
        verifyNoInteractions(projectAction);
    }

    @Test
    void thatUnirestConfiguredWithSslVerifyOnWhenAsked() throws Exception {
        uploadBomMojo.setVerifySsl(true);
        uploadBomMojo.execute();
        assertThat(Unirest.config().isVerifySsl(), is(equalTo(true)));
    }

    @Test
    void thatUnirestIsConfiguredWithSslVerifyOffWhenAsked() throws Exception {
        uploadBomMojo.setVerifySsl(false);
        uploadBomMojo.execute();
        assertThat(Unirest.config().isVerifySsl(), is(equalTo(false)));
    }

    @Test
    void thatWhenUpdateParentFailsTheLoggerIsCalledAndBuildFails() throws Exception {
        ModuleConfig config = new ModuleConfig();
        config.setProjectName("project-parent");
        config.setProjectVersion("1.2.3");
        config.setUpdateParent(true);

        doReturn(true).when(uploadBomAction).upload(config, false);

        uploadBomMojo.setModuleConfig(config);
        uploadBomMojo.setParentName("project-parent");
        uploadBomMojo.setParentVersion("1.2.3");
        uploadBomMojo.setUpdateParent(true);
        uploadBomMojo.setFailOnError(true);
        uploadBomMojo.setProjectTags(Collections.emptySet());
        uploadBomMojo.setUploadWithPut(false);

        try {
            uploadBomMojo.performAction();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(MojoExecutionException.class));
        }

        verify(logger).error("Failed to update project info");
        verify(mavenLogger).error(eq("Error occurred during upload"), any());
    }

    @Test
    void thatUpdateParentFailsWhenParentNameIsNull() throws Exception {
        doReturn(true).when(uploadBomAction).upload(moduleConfig, false);

        uploadBomMojo.setParentName(null);
        uploadBomMojo.setParentVersion(null);
        uploadBomMojo.setUpdateParent(true);
        uploadBomMojo.setFailOnError(true);
        uploadBomMojo.setProjectTags(Collections.emptySet());
        uploadBomMojo.setUploadWithPut(false);

        try {
            uploadBomMojo.performAction();
            fail("Exception expected");
        } catch (Exception ex) {
            assertThat(ex, instanceOf(MojoExecutionException.class));
        }

        verify(logger)
                .error("Parent update requested but no parent found in parent maven project or provided in "
                        + "config");
        verify(mavenLogger).error(eq("Error occurred during upload"), any());
    }

    @Test
    void thatUploadErrorsAreCorrectlyReported() throws Exception {
        DependencyTrackException cause = new DependencyTrackException("PKIX path building failed");
        doThrow(cause).when(uploadBomAction).upload(any(), anyBoolean());

        uploadBomMojo.setFailOnError(true);

        MojoExecutionException exception = assertThrows(MojoExecutionException.class, () -> uploadBomMojo.execute());
        assertThat(exception.getCause(), is(cause));

        verify(mavenLogger).error("Error occurred during upload", cause);
    }

    @Test
    void thatUploadErrorsIsReportedEvenWhenItShouldNotFail() throws Exception {
        DependencyTrackException cause = new DependencyTrackException("PKIX path building failed");
        doThrow(cause).when(uploadBomAction).upload(any(), anyBoolean());

        uploadBomMojo.execute();

        verify(mavenLogger).error("Error occurred during upload", cause);
    }
}
