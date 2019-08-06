package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.util.Logger;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

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
        doReturn(aProject()).when(projectAction).getProject(PROJECT_NAME, PROJECT_VERSION);
        doReturn(true).when(uploadBomAction).upload(anyString());

        uploadBomMojo.setProjectName(PROJECT_NAME);
        uploadBomMojo.setProjectVersion(PROJECT_VERSION);
        uploadBomMojo.execute();

        verify(uploadBomAction).upload(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), is(equalTo("./target/bom.xml")));
    }

    private Project aProject() {
        return new Project("1234", PROJECT_NAME, PROJECT_VERSION, null);
    }
}
