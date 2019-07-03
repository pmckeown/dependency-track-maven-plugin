package com.pmckeown;

import com.pmckeown.rest.client.DependencyTrackClient;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

abstract class AbstractDependencyTrackMojo extends AbstractMojo {

    @Parameter(required = true)
    private String dependencyTrackBaseUrl;

    @Parameter(required = true)
    private String apiKey;

    DependencyTrackClient dependencyTrackClient() {
        info("Connecting to Dependency Track instance: %s", dependencyTrackBaseUrl);
        return new DependencyTrackClient(dependencyTrackBaseUrl, apiKey);
    }

    void info(String message, Object... params) {
        if(getLog().isInfoEnabled()) {
            getLog().info(String.format(message, params));
        }
    }

    void debug(String message, Object... params) {
        if(getLog().isDebugEnabled()) {
            getLog().debug(String.format(message, params));
        }
    }

    void error(String message, Object... params) {
        if(getLog().isErrorEnabled()) {
            getLog().error(String.format(message, params));
        }
    }

    void setDependencyTrackBaseUrl(String url) {
        this.dependencyTrackBaseUrl = url;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
