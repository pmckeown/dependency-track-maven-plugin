package io.github.pmckeown.dependencytrack.suppressions;

import com.google.common.base.Throwables;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import kong.unirest.UnirestException;
//TODO whole class

/**
 * Handles uploading BOMs
 *
 * @author Paul McKeown
 */
@Singleton
public class SuppressionsAction {

    private final CommonConfig commonConfig;
    private final Logger logger;
    private AnalysisClient analysisClient;

    @Inject
    public SuppressionsAction(AnalysisClient analysisClient, CommonConfig commonConfig, Logger logger) {
        this.analysisClient = analysisClient;
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    public boolean setProjectSuppressions(Project project, List<Analysis> analysisList) throws DependencyTrackException {

        try {
            // send all analysis in list to Dependency Track
            for (Analysis analysis : analysisList) { doUpload(project.getUuid(), analysis); }
        } catch (UnirestException ex) {
            logger.error("Failed to configure vulnerability suppressions", ex);
            throw new DependencyTrackException("Failed to configure vulnerability suppressions");
        }
        return true;
    }

    private Optional<UploadAnalysisResponse> doUpload(String projectUuid, Analysis analysis) throws DependencyTrackException {
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
