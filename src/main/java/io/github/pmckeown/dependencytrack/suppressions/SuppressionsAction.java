package io.github.pmckeown.dependencytrack.suppressions;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles uploading a list of analyses to Dependency Track.
 *
 * @author Thomas Hucke
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

    public boolean setProjectSuppressions(List<Analysis> analysisList) throws DependencyTrackException {

        try {
            // send all analysis in list to Dependency Track
            for (Analysis analysis : analysisList) { doUpload(analysis); }
        } catch (Exception ex) {
            logger.error(String.format("Failed to configure vulnerability suppressions: %s", ex.getMessage()));
            throw new DependencyTrackException("Failed to configure vulnerability suppressions");
        }
        return true;
    }

    Optional<UploadAnalysisResponse> doUpload(Analysis analysis) throws DependencyTrackException {
        try {
            Response<UploadAnalysisResponse> response = analysisClient.uploadAnalysis(analysis);
            if (response.isSuccess()) {
                logger.info(
                    String.format("Analysis for vulnerability %s uploaded to Dependency Track server",
                        analysis.getVulnerabilityUuid()));
                return response.getBody();
            } else {
                String message = String.format("Failed to post analysis: %d %s with %s", response.getStatus(),
                    response.getStatusText(), analysis.toString());
                logger.error(message);
                throw new DependencyTrackException(message);
            }
        } catch (Exception ex) {
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }
}
