package com.pmckeown;

import com.pmckeown.rest.client.DependencyTrackClient;
import org.apache.maven.plugin.AbstractMojo;

abstract class AbstractDependencyTrackMojo extends AbstractMojo {

    private String host = "http://localhost:8080/api";
    private String apiKey = "CegP2X155YABba4gR805mVnbA9jRmQF1";
    private DependencyTrackClient dependencyTrackClient;

    AbstractDependencyTrackMojo() {
        this.dependencyTrackClient = new DependencyTrackClient(host, apiKey);
    }

    DependencyTrackClient dependencyTrackClient() {
        return this.dependencyTrackClient;
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
}
