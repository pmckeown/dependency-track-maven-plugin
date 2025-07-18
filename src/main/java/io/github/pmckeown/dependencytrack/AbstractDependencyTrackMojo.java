package io.github.pmckeown.dependencytrack;

import static io.github.pmckeown.dependencytrack.ObjectMapperFactory.relaxedObjectMapper;
import static kong.unirest.HeaderNames.ACCEPT;
import static kong.unirest.HeaderNames.ACCEPT_ENCODING;

import io.github.pmckeown.util.Logger;
import java.util.concurrent.atomic.AtomicBoolean;
import kong.unirest.Unirest;
import kong.unirest.jackson.JacksonObjectMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Base class for Mojos in this project.
 *
 * <p>Provides common configuration options:
 *
 * <ol>
 *   <li>projectName
 *   <li>projectVersion
 *   <li>dependencyTrackBaseUrl
 *   <li>apiKey
 *   <li>failOnError
 *   <li>skip
 *   <li>verifySsl
 * </ol>
 *
 * @author Paul McKeown
 */
public abstract class AbstractDependencyTrackMojo extends AbstractMojo {

    private static AtomicBoolean unirestConfiguration = new AtomicBoolean();

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

    /**
     * Set this to 'true' to bypass dependencyTrack plugin It's not a real boolean as it can have more
     * than 2 values:
     *
     * <ul>
     *   <li><code>true</code>: will skip as usual
     *   <li><code>releases</code>: will skip if current version of the project is a release
     *   <li><code>snapshots</code>: will skip if current version of the project is a snapshot
     *   <li>any other values will be considered as <code>false</code>
     * </ul>
     */
    @Parameter(defaultValue = "false", property = "dependency-track.skip", alias = "dependency-track.skip")
    private String skip = Boolean.FALSE.toString();

    @Parameter(defaultValue = "true", property = "dependency-track.verifySsl")
    private boolean verifySsl;

    @Parameter
    private PollingConfig pollingConfig;

    protected Logger logger;

    protected CommonConfig commonConfig;

    protected ModuleConfig moduleConfig;

    protected AbstractDependencyTrackMojo(CommonConfig commonConfig, ModuleConfig moduleConfig, Logger logger) {
        this.logger = logger;
        this.commonConfig = commonConfig;
        this.moduleConfig = moduleConfig;
    }

    /**
     * Initialises the {@link Logger} and {@link CommonConfig} instances that were injected by the
     * SISU inversion of control container (using Guice under the hood) by providing the data provided
     * by the Plexus IOC container.
     *
     * <p>Then performs the action defined by the subclass.
     */
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        // Set up Mojo environment
        this.logger.setLog(getLog());
        this.commonConfig.setDependencyTrackBaseUrl(dependencyTrackBaseUrl);
        this.commonConfig.setApiKey(apiKey);
        this.commonConfig.setPollingConfig(this.pollingConfig != null ? this.pollingConfig : PollingConfig.defaults());
        this.moduleConfig.setProjectName(projectName);
        this.moduleConfig.setProjectVersion(projectVersion);

        // Configure Unirest with additional user-supplied configuration
        configureUnirest();

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

    public void setVerifySsl(boolean verifySsl) {
        this.verifySsl = verifySsl;
    }

    public void setSkip(String skip) {
        this.skip = skip;
    }

    public void setPollingConfig(PollingConfig pollingConfig) {
        this.pollingConfig = pollingConfig;
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
        return Boolean.parseBoolean(skip)
                || ("releases".equals(skip) && !ArtifactUtils.isSnapshot(projectVersion))
                || ("snapshots".equals(skip) && ArtifactUtils.isSnapshot(projectVersion));
    }

    /**
     * Unirest is configured globally using a static `Unirest.config()` method. Doing so here allows
     * for user-supplied configuration.
     */
    private void configureUnirest() {
        if (unirestConfiguration.compareAndSet(false, true)) {
            Unirest.config()
                    .setObjectMapper(new JacksonObjectMapper(relaxedObjectMapper()))
                    .setDefaultHeader(ACCEPT_ENCODING, "gzip, deflate")
                    .setDefaultHeader(ACCEPT, "application/json")
                    .verifySsl(verifySsl);

            // Debug all Unirest config
            logger.debug("Unirest Configuration: %s", ToStringBuilder.reflectionToString(Unirest.config()));

            // Info print user specified
            logger.info("SSL Verification enabled: %b", verifySsl);
        }
    }

    /**
     * Provide access to the unirestConfiguration AtomicBoolean to allow tests to reset this config
     * object
     *
     * @return unirestConfiguration
     */
    public AtomicBoolean getUnirestConfiguration() {
        return unirestConfiguration;
    }
}
