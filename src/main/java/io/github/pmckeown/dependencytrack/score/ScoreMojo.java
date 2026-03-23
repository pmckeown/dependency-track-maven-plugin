package io.github.pmckeown.dependencytrack.score;

import static java.lang.String.format;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import org.apache.maven.api.Lifecycle.Phase;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the capability to find the current Inherited Risk Score as determined by the Dependency
 * Track Server.
 *
 * <p>Specific configuration options are:
 *
 * <ol>
 *   <li>inheritedRiskScoreThreshold
 * </ol>
 *
 * @author Paul McKeown
 */
@Mojo(name = "score", defaultPhase = Phase.VERIFY)
public class ScoreMojo extends AbstractDependencyTrackMojo {
    private static final Logger LOG = LoggerFactory.getLogger(ScoreMojo.class);

    @Parameter(property = "dependency-track.inheritedRiskScoreThreshold")
    private Integer inheritedRiskScoreThreshold;

    @Inject
    private ScoreAction scoreAction;

    @Override
    public void performAction() throws MojoFailureException, MojoExecutionException {
        try {
            Integer inheritedRiskScore = scoreAction.determineScore(moduleConfig, inheritedRiskScoreThreshold);
            failBuildIfThresholdIsBreached(inheritedRiskScore);
        } catch (DependencyTrackException ex) {
            handleFailure(format(
                    "Failed to determine score for: %s-%s",
                    moduleConfig.getProjectName(), moduleConfig.getProjectVersion()));
        }
    }

    private void failBuildIfThresholdIsBreached(Integer inheritedRiskScore) throws MojoFailureException {
        LOG.debug(
                "Inherited Risk Score Threshold set to: {}",
                inheritedRiskScoreThreshold == null ? "Not set" : inheritedRiskScoreThreshold);

        if (inheritedRiskScoreThreshold != null && inheritedRiskScore > inheritedRiskScoreThreshold) {

            throw new MojoFailureException(format(
                    "Inherited Risk Score [%d] was greater than the " + "configured threshold [%d]",
                    inheritedRiskScore, inheritedRiskScoreThreshold));
        }
    }

    /*
     * Setters for dependency injection in tests
     */
    void setInheritedRiskScoreThreshold(Integer threshold) {
        this.inheritedRiskScoreThreshold = threshold;
    }
}
