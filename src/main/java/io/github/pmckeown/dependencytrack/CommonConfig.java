package io.github.pmckeown.dependencytrack;

import io.github.pmckeown.util.Logger;
import java.util.Collections;
import java.util.Set;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;

/**
 * Holder for common configuration supplied on Mojo execution
 *
 * @author Paul McKeown
 */
@Singleton
public class CommonConfig {

    private String projectUuid="";
    private String projectName="";
    private String projectVersion="";
    private String dependencyTrackBaseUrl;
    private String apiKey;
    private PollingConfig pollingConfig;
    private String bomLocation;
    private MavenProject mavenProject;
    private boolean updateProjectInfo;
    private boolean updateParent;
    private String parentUuid;
    private String parentName;
    private String parentVersion;
    private boolean isLatest;
    private boolean autoCreate = true;
    private Set<String> projectTags = Collections.emptySet();

    protected Logger logger = new Logger(new SystemStreamLog());

    public String getProjectUuid() { return projectUuid; }

    public void setProjectUuid(String projectUuid) { this.projectUuid = projectUuid; }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public String getDependencyTrackBaseUrl() {
        return dependencyTrackBaseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public PollingConfig getPollingConfig() {
        return pollingConfig;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public void setDependencyTrackBaseUrl(String dependencyTrackBaseUrl) {
        this.dependencyTrackBaseUrl = dependencyTrackBaseUrl;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setPollingConfig(PollingConfig pollingConfig) {
        this.pollingConfig = pollingConfig;
    }
    public Set<String> getProjectTags() {
        return projectTags;
    }

    public void setProjectTags(Set<String> projectTags) {
        this.projectTags = projectTags;
    }

    public boolean isLatest() {
        return isLatest;
    }

    public void setLatest(boolean latest) {
        isLatest = latest;
    }

    public String getParentVersion() {
        return parentVersion;
    }

    public void setParentVersion(String parentVersion) {
        if (StringUtils.isBlank(parentUuid)) {
            this.parentVersion = parentVersion;
        } else if (StringUtils.isNotBlank(parentUuid))
            logger.info("parentUuid set so ignoring parentVersion: %s", parentVersion);
    }

    public String getParentUuid() { return parentUuid; }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
        if (StringUtils.isNotBlank(parentUuid)) {
            logger.info("parentUuid set to: %s", parentUuid);
            logger.info("clearing parentName and parentVersion");
            this.setParentName(null);
            this.setParentVersion(null);
        }
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        if (StringUtils.isBlank(parentUuid)) {
            this.parentName = parentName;
        } else if (StringUtils.isNotBlank(parentUuid))
            logger.info("parentUuid set so ignoring parentName: %s", parentName);
    }

    public boolean getUpdateParent() { return updateParent; }

    public void setUpdateParent(boolean updateParent) {
        this.updateParent = updateParent;
    }

    public boolean getUpdateProjectInfo() { return updateProjectInfo; }

    public void setUpdateProjectInfo(boolean updateProjectInfo) { this.updateProjectInfo = updateProjectInfo; }

    public MavenProject getMavenProject() { return mavenProject; }

    public void setMavenProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    public void setBomLocation(String bomLocation) {
        this.bomLocation = bomLocation;
    }
    public String getBomLocation() {
        if (StringUtils.isNotBlank(bomLocation)) {
            return bomLocation;
        } else {
            String defaultLocation = getMavenProject().getBasedir() + "/target/bom.xml";
            this.logger.debug("bomLocation not supplied so using: %s", defaultLocation);
            return defaultLocation;
        }
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }
}
