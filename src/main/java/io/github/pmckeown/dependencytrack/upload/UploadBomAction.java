package io.github.pmckeown.dependencytrack.upload;

import com.evanlennick.retry4j.exception.RetriesExhaustedException;
import com.evanlennick.retry4j.exception.UnexpectedException;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.Poller;
import io.github.pmckeown.dependencytrack.Response;
import java.io.File;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles uploading BOMs
 *
 * @author Paul McKeown
 */
@Named
@Singleton
public class UploadBomAction {
    private static final Logger LOG = LoggerFactory.getLogger(UploadBomAction.class);

    private BomClient bomClient;
    private CommonConfig commonConfig;
    private Poller<Boolean> poller;

    @Inject
    public UploadBomAction(BomClient bomClient, CommonConfig commonConfig) {
        this.bomClient = bomClient;
        this.poller = new Poller<>();
        this.commonConfig = commonConfig;
    }

    public boolean upload(ModuleConfig moduleConfig, boolean uploadWithPut) throws DependencyTrackException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Project Name: {}", moduleConfig.getProjectName());
            LOG.info("Project Version: {}", moduleConfig.getProjectVersion());
            LOG.info("Project is latest: {}", Boolean.TRUE.equals(moduleConfig.isLatest()));
            LOG.info("Project Tags: {}", StringUtils.join(moduleConfig.getProjectTags(), ","));
            LOG.info("Parent UUID: {}", moduleConfig.getParentUuid());
            LOG.info("Parent Name: {}", moduleConfig.getParentName());
            LOG.info("Parent Version: {}", moduleConfig.getParentVersion());
            LOG.info("{}", commonConfig.getPollingConfig());
        }

        Optional<BomReference> bomFileReference = createBomFileReference(moduleConfig.getBomLocation());
        if (!bomFileReference.isPresent()) {
            LOG.error("No bom.xml could be located at: {}", moduleConfig.getBomLocation());
            return false;
        }

        Optional<UploadBomResponse> uploadBomResponse = doUpload(moduleConfig, uploadWithPut, bomFileReference.get());

        if (commonConfig.getPollingConfig().isEnabled() && uploadBomResponse.isPresent()) {
            try {
                pollUntilBomIsProcessed(uploadBomResponse.get());
            } catch (UnexpectedException | RetriesExhaustedException ex) {
                LOG.error("Polling for processing completion was interrupted so continuing: {}", ex.getMessage(), ex);
            }
        }

        return true;
    }

    private void pollUntilBomIsProcessed(UploadBomResponse uploadBomResponse) {
        LOG.info("Checking for BOM analysis completion");
        poller.poll(commonConfig.getPollingConfig(), Boolean.TRUE, () -> {
            Response<BomProcessingResponse> response = bomClient.isBomBeingProcessed(uploadBomResponse.getToken());
            Optional<BomProcessingResponse> body = response.getBody();
            if (body.isPresent()) {
                boolean stillProcessing = body.get().isProcessing();
                LOG.info("Still processing: {}", stillProcessing);
                return stillProcessing;
            } else {
                return Boolean.TRUE;
            }
        });
    }

    private Optional<UploadBomResponse> doUpload(
            ModuleConfig moduleConfig, boolean uploadWithPut, BomReference bomFileReference)
            throws DependencyTrackException {
        try {
            Response<UploadBomResponse> response =
                    bomClient.uploadBom(new UploadBomRequest(moduleConfig, bomFileReference), uploadWithPut);

            if (response.isSuccess()) {
                LOG.info("BOM uploaded to Dependency Track server");
                return response.getBody();
            } else {
                String message = String.format(
                        "Failure integrating with Dependency Track: %d %s",
                        response.getStatus(), response.getStatusText());
                LOG.error(message);
                throw new DependencyTrackException(message);
            }
        } catch (Exception ex) {
            throw new DependencyTrackException(ex.getMessage(), ex);
        }
    }

    private Optional<BomReference> createBomFileReference(String bomLocation) {
        LOG.debug("Current working directory: {}", System.getProperty("user.dir"));
        LOG.debug("looking for bom.xml at {}", bomLocation);
        if (StringUtils.isBlank(bomLocation)) {
            return Optional.empty();
        }
        File file = new File(bomLocation);
        if (!file.canRead()) {
            return Optional.empty();
        } else {
            return Optional.of(new BomReference(file));
        }
    }
}
