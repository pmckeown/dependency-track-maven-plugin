package io.github.pmckeown.dependencytrack;

import io.github.pmckeown.dependencytrack.suppressions.VulnerabilitySuppression;
import io.github.pmckeown.util.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;

import java.util.Collections;
import java.util.Set;

/**
 * Holder for module dependent configuration supplied on Mojo execution
 *
 */
public class ModuleConfig {

    private String projectUuid = "";
    private String projectName = "";
    private String projectVersion = "";
    private String bomLocation;
    private String parentUuid;
    private String parentName;
    private String parentVersion;
    private MavenProject mavenProject;
    private boolean updateProjectInfo;
    private boolean updateParent;
    private Boolean isLatest;
    private boolean autoCreate = true;
    private Set<String> projectTags = Collections.emptySet();

    private Set<VulnerabilitySuppression> vulnerabilitySuppressions = Collections.emptySet();

    protected Logger logger = new Logger(new SystemStreamLog());

    public String getProjectUuid() {
        return projectUuid;
    }

    public void setProjectUuid(String projectUuid) {
        this.projectUuid = projectUuid;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
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

    public void setBomLocation(String bomLocation) {
        this.bomLocation = bomLocation;
    }

    public String getParentUuid() {
        return parentUuid;
    }

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

    public String getParentVersion() {
        return parentVersion;
    }

    public void setParentVersion(String parentVersion) {
        if (StringUtils.isBlank(parentUuid)) {
            this.parentVersion = parentVersion;
        } else if (StringUtils.isNotBlank(parentUuid))
            logger.info("parentUuid set so ignoring parentVersion: %s", parentVersion);
    }

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    public void setMavenProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    public boolean getUpdateParent() {
        return updateParent;
    }

    public void setUpdateParent(boolean updateParent) {
        this.updateParent = updateParent;
    }

    public boolean getUpdateProjectInfo() {
        return updateProjectInfo;
    }

    public void setUpdateProjectInfo(boolean updateProjectInfo) {
        this.updateProjectInfo = updateProjectInfo;
    }

    public Boolean isLatest() {
        return isLatest;
    }

    public void setLatest(Boolean latest) {
        isLatest = latest;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    public Set<String> getProjectTags() {
        return projectTags;
    }

    public void setProjectTags(Set<String> projectTags) {
        this.projectTags = projectTags;
    }

    public Set<VulnerabilitySuppression> getVulnerabilitySuppressions() { return vulnerabilitySuppressions; }

    public void setVulnerabilitySuppressions(Set<VulnerabilitySuppression> vulnerabilitySuppressions) {
        this.vulnerabilitySuppressions = vulnerabilitySuppressions;
    }
}
