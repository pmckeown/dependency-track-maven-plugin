package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.*;
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

    private static final int SLEEP_MILLISECONDS = 500;
    private static final int MAX_POLLS = 30;

    private BomClient bomClient;
    private BomEncoder bomEncoder;
    private CommonConfig commonConfig;
    private Logger logger;
    private Sleeper sleeper;

    @Inject
    public UploadBomAction(BomClient bomClient, BomEncoder bomEncoder, Sleeper sleeper, CommonConfig commonConfig,
               Logger logger) {
        this.bomClient = bomClient;
        this.bomEncoder = bomEncoder;
        this.sleeper = sleeper;
        this.commonConfig = commonConfig;
        this.logger = logger;
    }

    public boolean upload(String bomLocation) throws DependencyTrackException {
        logger.info("Project Name: %s", commonConfig.getProjectName());
        logger.info("Project Version: %s", commonConfig.getProjectVersion());
        logger.info("Plugin %s configured to wait for BOM processing to complete",
                commonConfig.getPollingConfig().isEnabled() ? "is" : "is not");

        Optional<String> encodedBomOptional = bomEncoder.encodeBom(bomLocation, logger);
        if (!encodedBomOptional.isPresent()) {
            logger.error("No bom.xml could be located at: %s", bomLocation);
            return false;
        }

        Optional<UploadBomResponse> uploadBomResponse = doUpload(encodedBomOptional.get());

        if (commonConfig.getPollingConfig().isEnabled() && uploadBomResponse.isPresent()) {
            try {
                pollUntilBomIsProcessed(uploadBomResponse.get());
            } catch (InterruptedException ex) {
                logger.error("Polling for processing completion was interrupted so continuing: %s",
                        ex.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        return true;
    }

    private void pollUntilBomIsProcessed(UploadBomResponse uploadBomResponse) throws InterruptedException {
        logger.info("Checking for BOM analysis completion");
        int counter = 0;
        isBomProcessed(uploadBomResponse.getToken(), counter);
    }

    /**
     * Recursive method to handle calling the server and checking whether processing is finished.  Will only execute
     * {@link UploadBomAction#MAX_POLLS} times and sleeps for {@link UploadBomAction#SLEEP_MILLISECONDS} each loop.
     *
     * @param bomToken the token used to check if the BOM is completely processed
     * @param counter an incrementing integer to cap the total number of calls
     * @throws InterruptedException if the thread performing the sleep is interrupted
     */
    private void isBomProcessed(String bomToken, int counter) throws InterruptedException {
        Response<BomProcessingResponse> response = bomClient.isBomBeingProcessed(bomToken);

        Optional<BomProcessingResponse> bomProcessingResponse = response.getBody();
        if (response.isSuccess() && bomProcessingResponse.isPresent()) {
            boolean stillProcessing = bomProcessingResponse.get().isProcessing();
            logger.info("Still processing: %b", stillProcessing);

            if (stillProcessing) {
                PollingConfig polling = commonConfig.getPollingConfig();
                sleeper.sleep(polling.getPause());
                if (counter < polling.getAttempts()) {
                    isBomProcessed(bomToken, counter + 1);
                } else {
                    logger.info("Max number of polling attempts [%d] reached, continuing with plugin execution",
                            polling.getAttempts());
                }
            }
        }
    }

    private Optional<UploadBomResponse> doUpload(String encodedBom) throws DependencyTrackException {
        try {
            Response<UploadBomResponse> response = bomClient.uploadBom(new UploadBomRequest(
                    commonConfig.getProjectName(), commonConfig.getProjectVersion(), true, encodedBom));
            
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
