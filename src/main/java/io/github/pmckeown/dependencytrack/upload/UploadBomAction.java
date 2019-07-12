package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.rest.model.Bom;
import io.github.pmckeown.rest.model.Response;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;

import java.util.Optional;

public class UploadBomAction { //extends DependencyTrackAction {

    private BomEncoder bomEncoder = new BomEncoder();
    private UploadBomClient uploadBomClient = new UploadBomClient();

    public boolean upload(UploadBomConfig config, Logger logger) throws DependencyTrackException {
        logger.info("Project Name: %s", config.common().getProjectName());
        logger.info("Project Version: %s", config.common().getProjectVersion());

        Optional<String> encodedBomOptional = bomEncoder.encodeBom(config.getBomLocation(), logger);
        if (!encodedBomOptional.isPresent()) {
            logger.error("No bom.xml could be located at: %s", config.getBomLocation());
            return false;
        }

        try {
            Response response = uploadBomClient.uploadBom(config, new Bom(config.common().getProjectName(),
                    config.common().getProjectVersion(), true, encodedBomOptional.get()));
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
