package io.github.pmckeown.dependencytrack;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import javax.inject.Inject;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;

@WireMockTest
@MojoTest
public abstract class AbstractDependencyTrackMojoTest {
    protected static final String TEST_PROJECT = "target/test-classes/projects/run";

    protected WireMockRuntimeInfo wireMockRuntimeInfo;

    @Inject
    protected MavenProject project;

    /**
     * Configure the mojo for testing. Will inject the mocked dependency track URL.
     *
     * @param mojo Mojo to configure
     */
    protected void configureMojo(AbstractDependencyTrackMojo mojo) {
        mojo.setDependencyTrackBaseUrl("http://localhost:" + wireMockRuntimeInfo.getHttpPort());
    }

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmri) {
        this.wireMockRuntimeInfo = wmri;

        Build build = project.getBuild();
        if (build == null) {
            build = mock();
            lenient().when(project.getBuild()).thenReturn(build);
            lenient().when(build.getDirectory()).thenReturn(TEST_PROJECT + "/target");
        }
    }
}
