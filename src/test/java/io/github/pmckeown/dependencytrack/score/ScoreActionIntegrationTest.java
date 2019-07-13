package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import io.github.pmckeown.rest.ResourceConstants;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.testing.SilentLog;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

// TODO - refactor to remove wiremock from these tests
@RunWith(MockitoJUnitRunner.class)
public class ScoreActionIntegrationTest extends AbstractDependencyTrackIntegrationTest {

    private static final int INHERITED_RISK_SCORE_THRESHOLD = 3;

    @InjectMocks
    private ScoreAction scoreAction;

    private Logger logger = new Logger(new SilentLog());

    @Ignore
    @Test
    public void thatScoreCanBeRetrieved() throws Exception {
        stubFor(get(urlEqualTo(ResourceConstants.V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        Integer score = scoreAction.determineScore(new ScoreConfig(getCommonConfig(), INHERITED_RISK_SCORE_THRESHOLD),
                logger);

        assertThat(score, is(equalTo(3)));
        verify(exactly(1), putRequestedFor(urlEqualTo(ResourceConstants.V1_BOM)));
    }
}
