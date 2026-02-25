package io.github.pmckeown.dependencytrack;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import org.apache.maven.api.Project;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.model.Build;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.junit.jupiter.api.BeforeEach;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest
@MojoTest
public abstract class AbstractDependencyTrackMojoTest {
    protected static final String TEST_PROJECT = "${basedir}/target/test-classes/projects/run";

    protected WireMockRuntimeInfo wireMockRuntimeInfo;

    @Inject
    protected Project project;

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
