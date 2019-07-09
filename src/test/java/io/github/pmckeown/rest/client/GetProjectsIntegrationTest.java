package io.github.pmckeown.rest.client;

import io.github.pmckeown.rest.model.GetProjectsResponse;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.github.pmckeown.rest.ResourceConstants.V1_PROJECT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test project retrieval
 *
 * @author Paul McKeown
 */
public class GetProjectsIntegrationTest extends AbstractDependencyTrackIntegrationTest {

    @Test
    public void thatAllProjectsCanBeRetrieved() throws Exception {
        stubFor(get(urlEqualTo(V1_PROJECT)).willReturn(
                aResponse().withBodyFile("api/v1/project/get-all-projects.json")));

        GetProjectsResponse response = dependencyTrackClient().getProjects();

        assertThat(response.getBody().size(), is(equalTo(7)));
        verify(exactly(1), getRequestedFor(urlEqualTo(V1_PROJECT)));
    }
}
