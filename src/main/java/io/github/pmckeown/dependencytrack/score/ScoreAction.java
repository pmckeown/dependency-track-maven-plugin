package io.github.pmckeown.dependencytrack.score;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.metrics.Metrics;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectClient;
import io.github.pmckeown.util.Logger;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Handles score retrieval and processing
 *
 * @author Paul McKeown
 */
public class ScoreAction {

    private static final String DELIMITER = "========================================================================";

    private ProjectClient scoreClient = new ProjectClient();
    private MetricsAction metricsAction = new MetricsAction();

    public Integer determineScore(ScoreConfig scoreConfig, Logger logger) throws DependencyTrackException {
        try {
            Response<List<Project>> response = scoreClient.getProjects(scoreConfig.common());

            Optional<List<Project>> body = response.getBody();
            if (response.isSuccess() && body.isPresent()) {
                return generateResult(body.get(), scoreConfig, logger);
            } else {
                throw new DependencyTrackException(format("Failed to get projects from Dependency Track: %d %s",
                        response.getStatus(), response.getStatusText()));
            }
        } catch (Exception ex) {
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }

    private Integer generateResult(List<Project> projects, ScoreConfig scoreConfig, Logger logger)
            throws DependencyTrackException {
        logger.debug(projects.toString());
        logger.debug("Found %s projects", projects.size());

        Optional<Project> projectOptional = findCurrentProject(projects, scoreConfig, logger);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();

            Metrics metrics = getMetricsFromProject(project, scoreConfig, logger);

            printInheritedRiskScore(project, metrics.getInheritedRiskScore(), scoreConfig, logger);

            return metrics.getInheritedRiskScore();

        } else {
            throw new DependencyTrackException(format("Failed to find project on server: Project: %s, Version: %s",
                    scoreConfig.common().getProjectName(), scoreConfig.common().getProjectVersion()));
        }
    }

    private Metrics getMetricsFromProject(Project project, ScoreConfig scoreConfig, Logger logger)
            throws DependencyTrackException {
        Metrics metrics = project.getMetrics();
        if (metrics != null) {
            return metrics;
        } else {
            logger.info("Metrics not present, checking the server for more info");
            return metricsAction.getMetrics(scoreConfig.common(), logger, project);
        }
    }

    private void printInheritedRiskScore(Project project, int inheritedRiskScore, ScoreConfig scoreConfig,
             Logger logger) {
        logger.info(DELIMITER);
        logger.info("Project: %s, Version: %s", project.getName(), project.getVersion());
        StringBuilder scoreMessage = new StringBuilder(format("Inherited Risk Score: %d", inheritedRiskScore));

        if (scoreConfig.getInheritedRiskScoreThreshold() != null) {
            scoreMessage.append(format(" - Maximum allowed Inherited Risk Score: %d",
                    scoreConfig.getInheritedRiskScoreThreshold()));
        }

        if (inheritedRiskScore > 0) {
            logger.warn(scoreMessage.toString());
        } else {
            logger.info(scoreMessage.toString());
        }
        logger.info(DELIMITER);
    }

    private Optional<Project> findCurrentProject(List<Project> projects, ScoreConfig scoreConfig, Logger logger) {
        logger.debug("Searching for project using Name: [%s] and Version [%s]",
                scoreConfig.common().getProjectName(), scoreConfig.common().getProjectVersion());
        return projects.stream()
                .parallel()
                .peek(project -> logger.debug(project.toString()))
                .filter(project -> project.getName().equals(scoreConfig.common().getProjectName()))
                .filter(project -> project.getVersion().equals(scoreConfig.common().getProjectVersion()))
                .findFirst();
    }
}
