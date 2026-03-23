package io.github.pmckeown.dependencytrack.score;

import static io.github.pmckeown.dependencytrack.Constants.DELIMITER;
import static java.lang.String.format;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.metrics.Metrics;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectClient;
import java.util.Optional;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles score retrieval and processing
 *
 * @author Paul McKeown
 */
@Named
@Singleton
class ScoreAction {
    private static final Logger LOG = LoggerFactory.getLogger(ScoreAction.class);

    private ProjectClient projectClient;
    private MetricsAction metricsAction;

    @Inject
    public ScoreAction(ProjectClient projectClient, MetricsAction metricsAction, CommonConfig commonConfig) {
        this.projectClient = projectClient;
        this.metricsAction = metricsAction;
    }

    Integer determineScore(ModuleConfig moduleConfig, Integer inheritedRiskScoreThreshold)
            throws DependencyTrackException {
        try {
            Response<Project> response = projectClient.getProject(
                    moduleConfig.getProjectUuid(), moduleConfig.getProjectName(), moduleConfig.getProjectVersion());

            Optional<Project> body = response.getBody();
            if (response.isSuccess() && body.isPresent()) {
                return generateResult(body.get(), inheritedRiskScoreThreshold);
            } else {
                throw new DependencyTrackException(format(
                        "Failed to get projects from Dependency Track: %d %s",
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
            LOG.info("Metrics not present, checking the server for more info");
            return metricsAction.getMetrics(project);
        }
    }

    private void printInheritedRiskScore(Project project, int inheritedRiskScore, Integer inheritedRiskScoreThreshold) {
        LOG.info(DELIMITER);
        LOG.info("Project: {}, Version: {}", project.getName(), project.getVersion());
        StringBuilder scoreMessage = new StringBuilder(format("Inherited Risk Score: %s", inheritedRiskScore));

        if (inheritedRiskScoreThreshold != null) {
            scoreMessage.append(format(" - Maximum allowed Inherited Risk Score: %s", inheritedRiskScoreThreshold));
        }

        if (inheritedRiskScore > 0) {
            LOG.warn("{}", scoreMessage);
        } else {
            LOG.info("{}", scoreMessage);
        }
        LOG.info(DELIMITER);
    }
}
