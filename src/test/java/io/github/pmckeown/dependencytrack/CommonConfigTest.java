package io.github.pmckeown.dependencytrack;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommonConfigTest {

    private static final String PROJECT_NAME = "test";

    private static final String PROJECT_VERSION = "1.0";

    @Mock
    private MavenProject project;

    @InjectMocks
    private CommonConfig commonConfig;

    @Before
    public void setup() {
        commonConfig = new CommonConfig();
        commonConfig.setMavenProject(project);
    }

    @Test
    public void thatTheBomLocationIsDefaultedWhenNotSupplied() {
        doReturn(new File(".")).when(project).getBasedir();

        assertThat(commonConfig.getBomLocation(), is(equalTo("./target/bom.xml")));
        assertThat(commonConfig.isLatest(), is(equalTo(false)));
    }
}
