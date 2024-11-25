package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.CommonConfig;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.github.pmckeown.dependencytrack.project.ProjectTag;

/**
 * Encapsulates the request payload for uploading a BOM
 *
 * @author Paul McKeown
 */
public class UploadBomRequest {

    private final UUID project;
    private final String projectName;
    private final String projectVersion;
    private final boolean autoCreate;
    private final String base64EncodedBom;
    private final boolean isLatest;
    private final List<ProjectTag> projectTags;
    private final UUID parentUUID;
    private final String parentName;
    private final String parentVersion;

    UploadBomRequest(CommonConfig commonConfig, String base64EncodedBom) {
        this.project = commonConfig.getProjectUuid();
        this.projectName = commonConfig.getProjectName();
        this.projectVersion = commonConfig.getProjectVersion();
        this.autoCreate = commonConfig.isAutoCreate();
        this.base64EncodedBom = base64EncodedBom;
        this.isLatest = commonConfig.isLatest();
        if (commonConfig.getProjectTags() == null) {
            this.projectTags = null;
        } else {
            this.projectTags = commonConfig.getProjectTags().stream().map(ProjectTag::new).collect(Collectors.toList());
        }
        this.parentUUID = commonConfig.getParentUuid();
        this.parentName = commonConfig.getParentName();
        this.parentVersion = commonConfig.getParentVersion();
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    /**
     * TODO: Change method name to IsisLatest, when switching to post upload request
     *
     * @return
     */
    public boolean isIsLatestProjectVersion() {
        return isLatest;
    }

    public String getBom() {
        return base64EncodedBom;
    }

    public List<ProjectTag> getProjectTags() {
        return projectTags;
    }

    public UUID getParentUUID() {
        return parentUUID;
    }

    public String getParentName() {
        return parentName;
    }

    public String getParentVersion() {
        return parentVersion;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
