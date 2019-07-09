package io.github.pmckeown.rest.client;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Client configuration tests
 *
 * @author Paul McKeown
 */
public class DependencyTrackClientTest {

    @Test
    public void thatProvidedBaseUrlsHaveTrailingSlashesRemoved() throws Exception {
        String url = "http://www.slashes-r-us.com/";

        DependencyTrackClient client = new DependencyTrackClient(url, "api123");

        assertThat(client.getHost(), is(equalTo("http://www.slashes-r-us.com")));
    }

    @Test
    public void thatProvidedBaseUrlsWithougTrailingSlashesAreNotModified() throws Exception {
        String url = "http://www.slashes-r-not-us.com";

        DependencyTrackClient client = new DependencyTrackClient(url, "api123");

        assertThat(client.getHost(), is(equalTo("http://www.slashes-r-not-us.com")));
    }
}
