package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/**
 * Handles uploading BOMs
 *
 * @author Paul McKeown
 */
@Singleton
public class UploadBomAction {

    private UploadBomClient uploadBomClient;
    private BomEncoder bomEncoder;
    private CommonConfig commonConfig;
    private Logger logger;

    @Inject
    public UploadBomAction(UploadBomClient uploadBomClient, BomEncoder bomEncoder, CommonConfig commonConfig,
           Logger logger) {
        this.uploadBomClient = uploadBomClient;
        this.bomEncoder = bomEncoder;
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    boolean upload(String bomLocation) throws DependencyTrackException {
        logger.info("Project Name: %s", commonConfig.getProjectName());
        logger.info("Project Version: %s", commonConfig.getProjectVersion());

        Optional<String> encodedBomOptional = bomEncoder.encodeBom(bomLocation, logger);
        if (!encodedBomOptional.isPresent()) {
            logger.error("No bom.xml could be located at: %s", bomLocation);
            return false;
        }

        try {
            Response response = uploadBomClient.uploadBom(new Bom(commonConfig.getProjectName(),
                    commonConfig.getProjectVersion(), true, encodedBomOptional.get()));
            if (response.isSuccess()) {
                logger.info("Bom uploaded to Dependency Track server");
            } else {
                logger.error("Failure integrating with Dependency Track: %d %s", response.getStatus(),
                        response.getStatusText());
            }
            return response.isSuccess();
        } catch (Exception ex) {
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }
}
