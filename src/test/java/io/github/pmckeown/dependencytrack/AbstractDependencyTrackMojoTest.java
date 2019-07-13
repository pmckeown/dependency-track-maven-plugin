package io.github.pmckeown.dependencytrack;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;

public abstract class AbstractDependencyTrackMojoTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    @Rule
    public MojoRule mojoRule = new MojoRule();
}
