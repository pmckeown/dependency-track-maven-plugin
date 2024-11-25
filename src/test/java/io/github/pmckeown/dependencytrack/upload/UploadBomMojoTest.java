package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import java.lang.reflect.Field;
import kong.unirest.Unirest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class UploadBomMojoTest {

    private static final String PROJECT_NAME = "test";

    private static final String PROJECT_VERSION = "1.0";

    @InjectMocks
    private UploadBomMojo uploadBomMojo;

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

    @Before
    public void setup() {
        uploadBomMojo.setMavenProject(project);
    }

    @Test
    public void thatTheBomLocationIsDefaultedWhenNotSupplied() throws Exception {
        doReturn(new File(".")).when(project).getBasedir();
        doReturn(aProject().build()).when(projectAction).getProject(PROJECT_NAME, PROJECT_VERSION);
        doReturn(true).when(uploadBomAction).upload();

        uploadBomMojo.setProjectName(PROJECT_NAME);
        uploadBomMojo.setProjectVersion(PROJECT_VERSION);
        uploadBomMojo.setProjectTags(Collections.emptySet());
        uploadBomMojo.execute();

        verify(uploadBomAction).upload();

        Field commonConfigField = uploadBomMojo.getClass().getDeclaredField("commonConfig");
        commonConfigField.setAccessible(true);
        CommonConfig commonConfig = (CommonConfig) commonConfigField.get(uploadBomMojo);
        assertThat(commonConfig.getBomLocation(), is(equalTo("./target/bom.xml")));
        assertThat(commonConfig.isLatest(), is(equalTo(false)));
    }

    @Test
    public void thatTheUploadBomIsSkippedWhenSkipIsTrue() throws Exception {
        uploadBomMojo.setSkip("true");
        uploadBomMojo.setProjectName(PROJECT_NAME);
        uploadBomMojo.setProjectVersion(PROJECT_VERSION);

        uploadBomMojo.execute();

        verify(commonConfig).setProjectName(PROJECT_NAME);
        verify(commonConfig).setProjectVersion(PROJECT_VERSION);
        verifyNoInteractions(uploadBomAction);
        verifyNoInteractions(metricsAction);
        verifyNoInteractions(projectAction);
    }

    @Test
    public void thatTheUploadBomIsSkippedWhenSkipIsReleases() throws Exception {
        uploadBomMojo.setSkip("releases");
        uploadBomMojo.setProjectName(PROJECT_NAME);
        uploadBomMojo.setProjectVersion(PROJECT_VERSION);

        uploadBomMojo.execute();

        verify(commonConfig).setProjectName(PROJECT_NAME);
        verify(commonConfig).setProjectVersion(PROJECT_VERSION);
        verifyNoInteractions(uploadBomAction);
        verifyNoInteractions(metricsAction);
        verifyNoInteractions(projectAction);
    }

    @Test
    public void thatTheUploadBomIsSkippedWhenSkipIsSnapshots() throws Exception {
        String snapshotVersion = "1.0-SNAPSHOT";
        uploadBomMojo.setSkip("snapshots");
        uploadBomMojo.setProjectName(PROJECT_NAME);
        uploadBomMojo.setProjectVersion(snapshotVersion);

        uploadBomMojo.execute();

        verify(commonConfig).setProjectName(PROJECT_NAME);
        verify(commonConfig).setProjectVersion(snapshotVersion);
        verifyNoInteractions(uploadBomAction);
        verifyNoInteractions(metricsAction);
        verifyNoInteractions(projectAction);
    }

    @Test
    public void thatUnirestConfiguredWithSslVerifyOnWhenAsked() throws Exception {
        uploadBomMojo.setVerifySsl(true);
        uploadBomMojo.execute();
        assertThat(Unirest.config().isVerifySsl(), is(equalTo(true)));
    }

    @Test
    public void thatUnirestIsConfiguredWithSslVerifyOffWhenAsked() throws Exception {
        uploadBomMojo.setVerifySsl(false);
        uploadBomMojo.execute();
        assertThat(Unirest.config().isVerifySsl(), is(equalTo(false)));
    }

    @Test
    public void thatWhenUpdateParentFailsTheLoggerIsCalledAndBuildFails() throws Exception {
        doReturn(true).when(uploadBomAction).upload();
        doReturn(aProject().withName("project-parent").withVersion("1.2.3").build())
                .when(projectAction).getProject("project-parent", "1.2.3");

        uploadBomMojo.setParentName("project-parent");
        uploadBomMojo.setParentVersion("1.2.3");
        uploadBomMojo.setUpdateParent(true);
        uploadBomMojo.setFailOnError(true);
        uploadBomMojo.setProjectTags(Collections.emptySet());

        try {
            uploadBomMojo.performAction();
        } catch (Exception ex) {
            assertThat(ex, instanceOf(MojoExecutionException.class));
        }

        verify(logger).error("Failed to update project info");
    }

    @Test
    public void thatUpdateParentFailsWhenParentNameIsNull() throws Exception {
        doReturn(true).when(uploadBomAction).upload();

        uploadBomMojo.setParentName(null);
        uploadBomMojo.setParentVersion(null);
        uploadBomMojo.setUpdateParent(true);
        uploadBomMojo.setFailOnError(true);
        uploadBomMojo.setProjectTags(Collections.emptySet());

        try {
            uploadBomMojo.performAction();
        } catch (Exception ex) {
            ex.printStackTrace();
            assertThat(ex, instanceOf(MojoExecutionException.class));
        }

        verify(logger).error("Parent update requested but no parent found in parent maven project or provided in " +
                "config");
    }
}
