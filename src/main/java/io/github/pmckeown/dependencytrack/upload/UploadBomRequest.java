package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.project.ProjectTag;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates the request payload for uploading a BOM
 *
 * @author Paul McKeown
 */
public class UploadBomRequest {

    private final String projectName;
    private final String projectVersion;
    private final boolean autoCreate;
    private final String base64EncodedBom;
    private final boolean isLatest;
    private final List<ProjectTag> projectTags;
    private final String parentUUID;
    private final String parentName;
    private final String parentVersion;

    UploadBomRequest(ModuleConfig moduleConfig, String base64EncodedBom) {
        this.projectName = moduleConfig.getProjectName();
        this.projectVersion = moduleConfig.getProjectVersion();
        this.autoCreate = moduleConfig.isAutoCreate();
        this.base64EncodedBom = base64EncodedBom;
        this.isLatest = moduleConfig.isLatest();
        if (moduleConfig.getProjectTags() == null) {
            this.projectTags = null;
        } else {
            this.projectTags = moduleConfig.getProjectTags().stream().map(ProjectTag::new).collect(Collectors.toList());
        }
        this.parentUUID = moduleConfig.getParentUuid();
        this.parentName = moduleConfig.getParentName();
        this.parentVersion = moduleConfig.getParentVersion();
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

    public String getParentUUID() {
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
