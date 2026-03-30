package io.github.pmckeown.dependencytrack.score;

import org.apache.maven.api.Lifecycle.Phase;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.DependencyTrackMojoException;

/**
 * Provides the capability to find the current Inherited Risk Score as determined by the Dependency Track Server.
 * <p>
 * Specific configuration options are:
 * <ol>
 * <li>inheritedRiskScoreThreshold
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
    public void performAction() throws DependencyTrackMojoException {
        try {
            Integer inheritedRiskScore = scoreAction.determineScore(moduleConfig, inheritedRiskScoreThreshold);
            failBuildIfThresholdIsBreached(inheritedRiskScore);
        } catch (DependencyTrackException ex) {
            handleFailure("Failed to determine score for: %s-%s".formatted(moduleConfig.getProjectName(), moduleConfig.getProjectVersion()));
        }
    }

    private void failBuildIfThresholdIsBreached(Integer inheritedRiskScore) throws InheritedRiskScoreException {
        LOG.debug("Inherited Risk Score Threshold set to: {}", inheritedRiskScoreThreshold == null ? "Not set" : inheritedRiskScoreThreshold);

        if (inheritedRiskScoreThreshold != null && inheritedRiskScore > inheritedRiskScoreThreshold) {
            throw new InheritedRiskScoreException("Inherited Risk Score exceeded threshold",
                    "Inherited Risk Score [%d] for %s-%s was greater than the configured threshold [%d]"
                            .formatted(inheritedRiskScore, moduleConfig.getProjectName(), moduleConfig.getProjectVersion(), inheritedRiskScoreThreshold));
        }
    }

    /*
     * Setters for dependency injection in tests
     */
    void setInheritedRiskScoreThreshold(Integer threshold) {
        this.inheritedRiskScoreThreshold = threshold;
    }
}
