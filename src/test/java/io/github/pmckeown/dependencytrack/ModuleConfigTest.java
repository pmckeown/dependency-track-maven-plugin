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
public class ModuleConfigTest {

    @Mock
    private MavenProject project;

    @InjectMocks
    private ModuleConfig moduleConfig;

    @Before
    public void setup() {
        moduleConfig = new ModuleConfig();
        moduleConfig.setMavenProject(project);
    }

    @Test
    public void thatTheBomLocationIsDefaultedWhenNotSupplied() {
        doReturn(new File(".")).when(project).getBasedir();

        assertThat(moduleConfig.getBomLocation(), is(equalTo("./target/bom.xml")));
    }
}
