package com.pmckeown.mojo;

import com.pmckeown.rest.client.DependencyTrackClient;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractDependencyTrackMojo extends AbstractMojo {

    @Parameter(required = true)
    private String dependencyTrackBaseUrl;

    @Parameter(required = true)
    private String apiKey;

    @Parameter(defaultValue = "false")
    protected boolean failOnError;

    protected DependencyTrackClient dependencyTrackClient() {
        info("Connecting to Dependency Track instance: %s", dependencyTrackBaseUrl);
        return new DependencyTrackClient(dependencyTrackBaseUrl, apiKey);
    }

    protected void info(String message, Object... params) {
        if(getLog().isInfoEnabled()) {
            getLog().info(String.format(message, params));
        }
    }

    protected void debug(String message, Object... params) {
        if(getLog().isDebugEnabled()) {
            getLog().debug(String.format(message, params));
        }
    }

    protected void error(String message, Object... params) {
        if(getLog().isErrorEnabled()) {
            getLog().error(String.format(message, params));
        }
    }

    public void setDependencyTrackBaseUrl(String url) {
        this.dependencyTrackBaseUrl = url;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setFailOnError(boolean fail) {
        this.failOnError = fail;
    }

    protected boolean shouldFailOnError() {
        return failOnError;
    }
}
