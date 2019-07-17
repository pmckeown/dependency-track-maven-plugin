package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackMojo;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.util.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.inject.Inject;

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
public class ScoreMojo extends DependencyTrackMojo {

    @Parameter
    private Integer inheritedRiskScoreThreshold;

    private ScoreAction scoreAction;

    @Inject
    public ScoreMojo(ScoreAction scoreAction, CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.scoreAction = scoreAction;
    }

    @Override
    public void execute() throws MojoFailureException, MojoExecutionException {
        super.execute();

        try {
            Integer inheritedRiskScore = scoreAction.determineScore(inheritedRiskScoreThreshold);
            failBuildIfThresholdIsBreached(inheritedRiskScore);
        } catch (DependencyTrackException ex) {
            handleFailure(format("Failed to determine score for: %s-%s", commonConfig.getProjectName(),
                    commonConfig.getProjectVersion()));
        }
    }

    private void failBuildIfThresholdIsBreached(Integer inheritedRiskScore) throws MojoFailureException {
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
