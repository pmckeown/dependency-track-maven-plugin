package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.CommonConfig;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UploadBomMojoTest {

    @InjectMocks
    private UploadBomMojo uploadBomMojo;

    @Mock
    private MavenProject project;

    @Mock
    private UploadBomAction uploadBomAction;

    @Mock
    private Logger logger;

    @Mock
    private CommonConfig commonConfig;

    @Before
    public void setup() {
        uploadBomMojo.setProject(project);
    }

    @Test
    public void thatTheBomLocationIsDefaultedWhenNotSupplied() throws Exception {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        doReturn(new File(".")).when(project).getBasedir();

        uploadBomMojo.execute();

        verify(uploadBomAction).upload(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), is(equalTo("./target/bom.xml")));
    }
}
