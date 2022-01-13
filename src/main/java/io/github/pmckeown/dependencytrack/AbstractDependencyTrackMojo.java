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
 *     <li>skip</li>
 * </ol>
 *
 * @author Paul McKeown
 */
public abstract class AbstractDependencyTrackMojo extends AbstractMojo {

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

    @Parameter
    private PollingConfig pollingConfig;

    @Parameter(defaultValue = "false", property = "dependency-track.skip", alias = "dependency-track.skip")
    private boolean skip;

    protected Logger logger;

    protected CommonConfig commonConfig;

    protected AbstractDependencyTrackMojo(CommonConfig commonConfig, Logger logger) {
        this.logger = logger;
        this.commonConfig = commonConfig;
    }

    /**
     * Initialises the {@link Logger} and {@link CommonConfig} instances that were injected by the SISU inversion of
     * control container (using Guice under the hood) by providing the data provided by the Plexus IOC container.
     *
     * Then performs the action defined by the subclass.
     */
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        // Set up Mojo environment
        this.logger.setLog(getLog());
        this.commonConfig.setProjectName(projectName);
        this.commonConfig.setProjectVersion(projectVersion);
        this.commonConfig.setDependencyTrackBaseUrl(dependencyTrackBaseUrl);
        this.commonConfig.setApiKey(apiKey);
        this.commonConfig.setPollingConfig(this.pollingConfig != null ? this.pollingConfig : PollingConfig.defaults());

        // Perform the requested action
        if (getSkip()) {
            logger.info("dependency-track.skip = true: Skipping analysis.");
            return;
        }
        this.performAction();
    }

    /**
     * Template method to be implemented by subclasses.
     *
     * @throws MojoExecutionException when an error is encountered during Mojo execution
     * @throws MojoFailureException when the Mojo fails
     */
    protected abstract void performAction() throws MojoExecutionException, MojoFailureException;

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

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public void setPollingConfig(PollingConfig commonConfig) {
        this.pollingConfig = commonConfig;
    }

    protected void handleFailure(String message) throws MojoFailureException {
        getLog().error(message);
        if (failOnError) {
            throw new MojoFailureException(message);
        }
    }

    protected void handleFailure(String message, Throwable ex) throws MojoExecutionException {
        getLog().debug(message, ex);
        if (failOnError) {
            throw new MojoExecutionException(message);
        }
    }

    private boolean getSkip() {
        return Boolean.parseBoolean(System.getProperty("dependency-track.skip", Boolean.toString(skip)));
    }
}
