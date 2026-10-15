package io.github.pmckeown.dependencytrack;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.maven.api.Project;
import org.apache.maven.api.di.Inject;
import org.apache.maven.testing.plugin.MojoTest;
import org.junit.jupiter.api.BeforeEach;

@WireMockTest
@MojoTest
public abstract class AbstractDependencyTrackMojoTest {
    protected WireMockRuntimeInfo wireMockRuntimeInfo;

    @Inject
    protected Project project;

    /**
     * Configure the mojo for testing. Will inject the mocked dependency track URL.
     *
     * @param mojo Mojo to configure
     */
    protected void configureMojo(AbstractDependencyTrackMojo mojo) {
        mojo.setDependencyTrackBaseUrl(wireMockRuntimeInfo.getHttpBaseUrl());
    }

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmri) {
        this.wireMockRuntimeInfo = wmri;
    }
}
