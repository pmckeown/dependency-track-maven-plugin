package io.github.pmckeown.dependencytrack.upload;

import com.evanlennick.retry4j.exception.RetriesExhaustedException;
import com.evanlennick.retry4j.exception.UnexpectedException;
import io.github.pmckeown.dependencytrack.*;
import io.github.pmckeown.util.BomEncoder;
import io.github.pmckeown.util.Logger;
import org.apache.commons.lang3.StringUtils;

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

    private BomClient bomClient;
    private BomEncoder bomEncoder;
    private CommonConfig commonConfig;
    private Logger logger;
    private Poller<Boolean> poller;

    @Inject
    public UploadBomAction(BomClient bomClient, BomEncoder bomEncoder, Poller<Boolean> poller,
                           CommonConfig commonConfig, Logger logger) {
        this.bomClient = bomClient;
        this.bomEncoder = bomEncoder;
        this.poller = poller;
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    public boolean upload(ModuleConfig moduleConfig) throws DependencyTrackException {
        logger.info("Project Name: %s", moduleConfig.getProjectName());
        logger.info("Project Version: %s", moduleConfig.getProjectVersion());
        logger.info("Project is latest: %s", moduleConfig.isLatest());
        logger.info("Project Tags: %s", StringUtils.join(moduleConfig.getProjectTags(), ","));
        logger.info("Parent UUID: %s", moduleConfig.getParentUuid());
        logger.info("Parent Name: %s", moduleConfig.getParentName());
        logger.info("Parent Version: %s", moduleConfig.getParentVersion());
        logger.info("%s", commonConfig.getPollingConfig());

        Optional<String> encodedBomOptional = bomEncoder.encodeBom(moduleConfig.getBomLocation(), logger);
        if (!encodedBomOptional.isPresent()) {
            logger.error("No bom.xml could be located at: %s", moduleConfig.getBomLocation());
            return false;
        }

        Optional<UploadBomResponse> uploadBomResponse = doUpload(moduleConfig, encodedBomOptional.get());

        if (commonConfig.getPollingConfig().isEnabled() && uploadBomResponse.isPresent()) {
            try {
                pollUntilBomIsProcessed(uploadBomResponse.get());
            } catch (UnexpectedException | RetriesExhaustedException ex) {
                logger.error("Polling for processing completion was interrupted so continuing: %s",
                        ex.getMessage());
            }
        }

        return true;
    }

    private void pollUntilBomIsProcessed(UploadBomResponse uploadBomResponse) {
        logger.info("Checking for BOM analysis completion");
        poller.poll(commonConfig.getPollingConfig(), Boolean.TRUE, () -> {
            Response<BomProcessingResponse> response = bomClient.isBomBeingProcessed(uploadBomResponse.getToken());
            Optional<BomProcessingResponse> body = response.getBody();
            if (body.isPresent()) {
                boolean stillProcessing = body.get().isProcessing();
                logger.info("Still processing: %b", stillProcessing);
                return stillProcessing;
            } else {
                return Boolean.TRUE;
            }
        });
    }

    private Optional<UploadBomResponse> doUpload(ModuleConfig moduleConfig, String encodedBom) throws DependencyTrackException {
        try {
            Response<UploadBomResponse> response = bomClient.uploadBom(
                    new UploadBomRequest(moduleConfig, encodedBom)
            );

            if (response.isSuccess()) {
                logger.info("BOM uploaded to Dependency Track server");
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
