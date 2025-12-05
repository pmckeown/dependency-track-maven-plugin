package io.github.pmckeown.dependencytrack;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModuleConfigTest {

    @Mock
    MavenProject project;

    @InjectMocks
    ModuleConfig moduleConfig;

    @BeforeEach
    void setup() {
        moduleConfig = new ModuleConfig();
        moduleConfig.setMavenProject(project);
    }

    @Test
    void thatTheBomLocationIsDefaultedWhenNotSupplied() {
        doReturn(new File(".")).when(project).getBasedir();

        assertThat(moduleConfig.getBomLocation(), is(equalTo("./target/bom.xml")));
    }
}
