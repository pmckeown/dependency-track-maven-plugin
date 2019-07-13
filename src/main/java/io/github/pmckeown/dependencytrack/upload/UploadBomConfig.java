package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.CommonConfig;

/**
 * Config class for the {@link UploadBomAction}
 *
 * @author Paul McKeown
 */
class UploadBomConfig {

    private CommonConfig commonConfig;
    private String bomLocation;

    public UploadBomConfig(CommonConfig commonConfig, String bomLocation) {
        this.commonConfig = commonConfig;
        this.bomLocation = bomLocation;
    }

    public CommonConfig common() {
        return commonConfig;
    }

    public String getBomLocation() {
        return bomLocation;
    }
}
