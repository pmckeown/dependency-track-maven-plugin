package io.github.pmckeown.dependencytrack.upload;

import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.dependencytrack.project.ProjectInfo;
import io.github.pmckeown.util.Logger;

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
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        doReturn(new File(".")).when(project).getBasedir();
        doReturn(aProject().build()).when(projectAction).getProject(PROJECT_NAME, PROJECT_VERSION);
        doReturn(true).when(uploadBomAction).upload(anyString());

        uploadBomMojo.setProjectName(PROJECT_NAME);
        uploadBomMojo.setProjectVersion(PROJECT_VERSION);
        uploadBomMojo.execute();

        verify(uploadBomAction).upload(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), is(equalTo("./target/bom.xml")));
    }

    @Test
    public void thatTheUploadBomIsSkippedWhenSkipIsTrue() throws Exception {
        uploadBomMojo.setSkip(true);
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
    public void projectInfoCreationFromSbom() {
        uploadBomMojo.setBomLocation(UploadBomMojoTest.class.getResource("bom.xml").getFile());
        ProjectInfo info = uploadBomMojo.createProjectInfo().get();
        assertThat(info.getGroup(), is(equalTo("io.github.pmckeown")));
        assertThat(info.getDescription(), is(equalTo("Maven plugin to integrate with a Dependency Track server to submit dependency manifests and gather project metrics.")));
        assertThat(info.getPurl(), is(equalTo("pkg:maven/io.github.pmckeown/dependency-track-maven-plugin@1.2.1-SNAPSHOT?type=maven-plugin")));
        assertThat(info.getClassifier(), is(equalTo("LIBRARY")));
    }

    @Test
    public void thatProjectInfoCreationFromMissingSbomThrowsNoException() {
        assertThat(uploadBomMojo.createProjectInfo().isPresent(), is(equalTo(false)));
    }

    @Test
    public void thatProjectInfoCreationFromOldSbomReturnsNoProjectInfo() {
        uploadBomMojo.setBomLocation(UploadBomMojoTest.class.getResource("bom-1.1.xml").getFile());
        assertThat(uploadBomMojo.createProjectInfo().isPresent(), is(equalTo(false)));
    }
}
