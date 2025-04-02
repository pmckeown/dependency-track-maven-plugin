package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.project.ProjectTag;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private final Boolean isLatest;
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
        if (CollectionUtils.isEmpty(moduleConfig.getProjectTags())) {
            this.projectTags = null;
        } else {
            this.projectTags = moduleConfig.getProjectTags().stream().map(ProjectTag::new).collect(Collectors.toList());
        }
        this.parentUUID = moduleConfig.getParentUuid();
        if (StringUtils.isNoneBlank(moduleConfig.getParentName(), moduleConfig.getParentVersion())) {
            // For new project versions both values are required in order to resolve the parent.
            this.parentName = moduleConfig.getParentName();
            this.parentVersion = moduleConfig.getParentVersion();
        } else {
            this.parentName = null;
            this.parentVersion = null;
        }
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("isLatestProjectVersion")
    public Boolean getIsLatest() {
        return isLatest;
    }

    public String getBom() {
        return base64EncodedBom;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
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
