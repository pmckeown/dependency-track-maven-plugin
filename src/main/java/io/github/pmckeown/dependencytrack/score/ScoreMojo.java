package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static java.lang.String.format;

/**
 * Provides the capability to find the current Inherited Risk Score as determined by the Dependency Track Server.
 *
 * Specific configuration options are:
 * <ol>
 *     <li>inheritedRiskScoreThreshold</li>
 * </ol>
 *
 * @author Paul McKeown
 */
@Mojo(name = "score", defaultPhase = LifecyclePhase.VERIFY)
public class ScoreMojo extends AbstractDependencyTrackMojo {

    @Parameter
    private Integer inheritedRiskScoreThreshold;

    private ScoreAction scoreAction = new ScoreAction();

    @Override
    public void execute() throws MojoFailureException, MojoExecutionException {
        ScoreConfig scoreConfig = new ScoreConfig(commonConfig(), inheritedRiskScoreThreshold);
        Logger logger = new Logger(getLog());

        try {
            Integer inheritedRiskScore = scoreAction.determineScore(scoreConfig, logger);
            if (inheritedRiskScore == null) {
                handleFailure(format("Failed to determine score for: %s-%s", commonConfig().getProjectName(),
                        commonConfig().getProjectVersion()));
            } else {
                failBuildIfThresholdIsBreached(inheritedRiskScore, logger);
            }
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred while determining score", ex);
        }
    }

    private void failBuildIfThresholdIsBreached(Integer inheritedRiskScore, Logger logger) throws MojoFailureException {
        logger.debug("Inherited Risk Score Threshold set to: %s",
                inheritedRiskScoreThreshold == null ? "Not set" : inheritedRiskScoreThreshold);

        if (inheritedRiskScoreThreshold != null && inheritedRiskScore > inheritedRiskScoreThreshold) {

            throw new MojoFailureException(format("Inherited Risk Score [%d] was greater than the " +
                    "configured threshold [%d]", inheritedRiskScore, inheritedRiskScoreThreshold));
        }
    }

    /*
     * Setters for dependency injection in tests
     */
    void setInheritedRiskScoreThreshold(Integer threshold) {
        this.inheritedRiskScoreThreshold = threshold;
    }
}
