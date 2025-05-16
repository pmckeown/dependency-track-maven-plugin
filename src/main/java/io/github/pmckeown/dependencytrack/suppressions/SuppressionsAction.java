package io.github.pmckeown.dependencytrack.suppressions;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles uploading BOMs
 *
 * @author Paul McKeown
 */
@Singleton
public class SuppressionsAction {

    private final Logger logger;
    private final AnalysisClient analysisClient;

    @Inject
    public SuppressionsAction(AnalysisClient analysisClient, Logger logger) {
        this.analysisClient = analysisClient;
        this.logger = logger;
    }

    public boolean setProjectSuppressions(Project project, List<Analysis> analysisList) throws DependencyTrackException {

        try {
            // send all analysis in list to Dependency Track
            for (Analysis analysis : analysisList) { doUpload(project.getUuid(), analysis); }
        } catch (Exception ex) {
            logger.error("Failed to configure vulnerability suppressions", ex);
            throw new DependencyTrackException("Failed to configure vulnerability suppressions");
        }
        return true;
    }

    Optional<UploadAnalysisResponse> doUpload(String projectUuid, Analysis analysis) throws DependencyTrackException {
        try {
            Response<UploadAnalysisResponse> response = analysisClient.uploadAnalysis(projectUuid, analysis);

            if (response.isSuccess()) {
                logger.info("Analysis uploaded to Dependency Track server");
                return response.getBody();
            } else {
                String message = String.format("Failure integrating with Dependency Track: %d %s", response.getStatus(),
                    response.getStatusText());
                logger.error(message);
                throw new DependencyTrackException(message);
            }
        } catch (Exception ex) {
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }
}
