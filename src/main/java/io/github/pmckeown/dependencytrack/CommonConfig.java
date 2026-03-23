package io.github.pmckeown.dependencytrack;

import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;

/**
 * Holder for common configuration supplied on Mojo execution
 *
 * @author Paul McKeown
 */
@Named
@Singleton
public class CommonConfig {

    private String dependencyTrackBaseUrl;
    private String apiKey;
    private PollingConfig pollingConfig;

    public String getDependencyTrackBaseUrl() {
        return dependencyTrackBaseUrl;
    }

    public void setDependencyTrackBaseUrl(String dependencyTrackBaseUrl) {
        this.dependencyTrackBaseUrl = dependencyTrackBaseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public PollingConfig getPollingConfig() {
        return pollingConfig;
    }

    public void setPollingConfig(PollingConfig pollingConfig) {
        this.pollingConfig = pollingConfig;
    }
}
