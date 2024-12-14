package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.metrics.Metrics;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectClient;
import io.github.pmckeown.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;
import static java.lang.String.format;

/**
 * Handles score retrieval and processing
 *
 * @author Paul McKeown
 */
@Singleton
class ScoreAction {

    private ProjectClient projectClient;
    private MetricsAction metricsAction;
    private CommonConfig commonConfig = new CommonConfig();
    private Logger logger;

    @Inject
    public ScoreAction(ProjectClient projectClient, MetricsAction metricsAction, CommonConfig commonConfig,
           Logger logger) {
        this.projectClient = projectClient;
        this.metricsAction = metricsAction;
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    Integer determineScore(Integer inheritedRiskScoreThreshold) throws DependencyTrackException {
        try {
            Response<Project> response = projectClient.getProject(commonConfig.getProjectUuid(), commonConfig.getProjectName(), commonConfig.getProjectVersion());

            Optional<Project> body = response.getBody();
            if (response.isSuccess() && body.isPresent()) {
                return generateResult(body.get(), inheritedRiskScoreThreshold);
            } else {
                throw new DependencyTrackException(format("Failed to get projects from Dependency Track: %d %s",
                        response.getStatus(), response.getStatusText()));
            }
        } catch (Exception ex) {
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }

    private Integer generateResult(Project project, Integer inheritedRiskScoreThreshold)
            throws DependencyTrackException {
        Metrics metrics = getMetricsFromProject(project);

        printInheritedRiskScore(project, metrics.getInheritedRiskScore(), inheritedRiskScoreThreshold);

        return metrics.getInheritedRiskScore();
    }

    private Metrics getMetricsFromProject(Project project) throws DependencyTrackException {
        Metrics metrics = project.getMetrics();
        if (metrics != null) {
            return metrics;
        } else {
            logger.info("Metrics not present, checking the server for more info");
            return metricsAction.getMetrics(project);
        }
    }

    private void printInheritedRiskScore(Project project, int inheritedRiskScore, Integer inheritedRiskScoreThreshold) {
        logger.info(DELIMITER);
        logger.info("Project: %s, Version: %s", project.getName(), project.getVersion());
        StringBuilder scoreMessage = new StringBuilder(format("Inherited Risk Score: %d", inheritedRiskScore));

        if (inheritedRiskScoreThreshold != null) {
            scoreMessage.append(format(" - Maximum allowed Inherited Risk Score: %d", inheritedRiskScoreThreshold));
        }

        if (inheritedRiskScore > 0) {
            logger.warn(scoreMessage.toString());
        } else {
            logger.info(scoreMessage.toString());
        }
        logger.info(DELIMITER);
    }

    /*
     * Setters for dependency injection in tests
     */
    void setCommonConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }
}
