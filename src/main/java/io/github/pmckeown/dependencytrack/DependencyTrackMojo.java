package io.github.pmckeown.dependencytrack;

import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Base class for Mojos in this project.
 *
 * Provides common configuration options:
 * <ol>
 *     <li>projectName</li>
 *     <li>projectVersion</li>
 *     <li>dependencyTrackBaseUrl</li>
 *     <li>apiKey</li>
 *     <li>failOnError</li>
 * </ol>
 *
 * @author Paul McKeown
 */
// TODO - refactor into a template method so that the execute() method cannot be forgotten
public class DependencyTrackMojo extends AbstractMojo {

    @Parameter(required = true, defaultValue = "${project.artifactId}", property = "dependency-track.projectName")
    protected String projectName;

    @Parameter(required = true, defaultValue = "${project.version}", property = "dependency-track.projectVersion")
    protected String projectVersion;

    @Parameter(required = true, property = "dependency-track.dependencyTrackBaseUrl")
    private String dependencyTrackBaseUrl;

    @Parameter(required = true, property = "dependency-track.apiKey")
    private String apiKey;

    @Parameter(defaultValue = "false", property = "dependency-track.failOnError")
    private boolean failOnError;

    protected Logger logger;

    protected CommonConfig commonConfig;

    protected DependencyTrackMojo(CommonConfig commonConfig, Logger logger) {
        this.logger = logger;
        this.commonConfig = commonConfig;
    }

    /**
     * Initialises the {@link Logger} and {@link CommonConfig} instances that were injected by the SISU inversion of
     * control container (using Guice under the hood) by providing the data provided by the Plexus IOC container.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.logger.setLog(getLog());
        this.commonConfig.setProjectName(projectName);
        this.commonConfig.setProjectVersion(projectVersion);
        this.commonConfig.setDependencyTrackBaseUrl(dependencyTrackBaseUrl);
        this.commonConfig.setApiKey(apiKey);
        this.commonConfig.setFailOnError(failOnError);
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
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

    protected void handleFailure(String message) throws MojoFailureException {
        getLog().error(message);
        if (failOnError) {
            throw new MojoFailureException(message);
        }
    }

    protected void handleFailure(String message, Throwable ex) throws MojoExecutionException {
        getLog().error(message, ex);
        if (failOnError) {
            throw new MojoExecutionException(message);
        }
    }

}
