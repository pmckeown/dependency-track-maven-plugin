package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.dependencytrack.project.UpdateRequest;
import io.github.pmckeown.util.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.Set;

import javax.inject.Inject;

/**
 * Provides the capability to upload a Bill of Material (BOM) to your Dependency Track server.
 *
 * The BOM may any format supported by your Dependency Track server, has only been tested with the output from the
 * <a href="https://github.com/CycloneDX/cyclonedx-maven-plugin">cyclonedx-maven-plugin</a> in the
 * <a href="https://cyclonedx.org/">CycloneDX</a> format
 *
 * Specific configuration options are:
 * <ol>
 *     <li>bomLocation</li>
 * </ol>
 *
 * @author Paul McKeown
 */
@Mojo(name = "upload-bom", defaultPhase = LifecyclePhase.VERIFY)
public class UploadBomMojo extends AbstractDependencyTrackMojo {

    @Parameter(property = "dependency-track.bomLocation")
    private String bomLocation;

    @Parameter(property = "project", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(property = "dependency-track.updateProjectInfo")
    private boolean updateProjectInfo;

    @Parameter(property = "dependency-track.updateParent")
    private boolean updateParent;

    @Parameter(defaultValue = "${project.parent.uuid}", property = "dependency-track.parentUuid")
    private String parentUuid;

    @Parameter(defaultValue = "${project.parent.name}", property = "dependency-track.parentName")
    private String parentName;

    @Parameter(property = "dependency-track.parentVersion")
    private String parentVersion;

    @Parameter(property = "dependency-track.isLatest", defaultValue = "false")
    private boolean isLatest;

    @Parameter(property = "dependency-track.projectTags")
    private Set<String> projectTags;

    private final UploadBomAction uploadBomAction;

    private final MetricsAction metricsAction;

    private final ProjectAction projectAction;

    @Inject
    public UploadBomMojo(UploadBomAction uploadBomAction, MetricsAction metricsAction, ProjectAction projectAction,
             CommonConfig commonConfig, Logger logger) {
        super(commonConfig, logger);
        this.uploadBomAction = uploadBomAction;
        this.metricsAction = metricsAction;
        this.projectAction = projectAction;
    }

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {
        enrichCommonConfig();
        logger.info("Update Project Parent : %s", commonConfig.getUpdateParent());

        try {
            if (!uploadBomAction.upload()) {
                handleFailure("Bom upload failed");
            }
            Project project = projectAction.getProject(commonConfig);

            UpdateRequest updateReq = new UpdateRequest();
            if (updateProjectInfo) {
                updateReq.withBomLocation(getBomLocation());
            }
            if (updateParent) {
                updateReq.withParent(getProjectParent(commonConfig));
            }
            if (updateProjectInfo || updateParent) {
                boolean projectUpdated = projectAction.updateProject(project, updateReq, projectTags);
                if (!projectUpdated) {
                    logger.error("Failed to update project info");
                    throw new DependencyTrackException("Failed to update project info");
                }
            }

            metricsAction.refreshMetrics(project);
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred during upload", ex);
        }
    }

    private void enrichCommonConfig() {
        this.commonConfig.setBomLocation(getBomLocation());
        this.commonConfig.setMavenProject(mavenProject);
        this.commonConfig.setUpdateProjectInfo(updateProjectInfo);
        this.commonConfig.setUpdateParent(updateParent);
        this.commonConfig.setParentUuid(parentUuid);
        this.commonConfig.setParentName(parentName);
        this.commonConfig.setParentVersion(parentVersion);
        this.commonConfig.setLatest(isLatest);
        this.commonConfig.setProjectTags(projectTags);
    }
    private Project getProjectParent(CommonConfig commonConfig) throws DependencyTrackException {
        if (StringUtils.isBlank(commonConfig.getParentName()) && StringUtils.isBlank(commonConfig.getParentUuid())) {
            logger.error("Parent update requested but no parent found in parent maven project or provided in config");
            throw new DependencyTrackException("No parent configured.");
        } else {
            if (StringUtils.isBlank(commonConfig.getParentUuid()))
                return getProjectParentByNameAndVersion(commonConfig.getParentName(), commonConfig.getParentVersion());
            else
                return getProjectParentByUuid(commonConfig.getParentUuid());
        }
    }

    private Project getProjectParentByUuid(String uuid) throws DependencyTrackException {
        logger.info("Attempting to fetch project parent: '%s'", uuid);
        try {
            return projectAction.getProject(uuid);
        } catch (DependencyTrackException ex) {
            logger.error("Failed to find parent project with UUID ['%s']. Check the update parent " +
                "your settings for this plugin and verify if a matching parent project exists in the " +
                "server.", uuid);
            throw ex;
        }
    }

    private Project getProjectParentByNameAndVersion(String name, String version) throws DependencyTrackException {
        logger.info("Attempting to fetch project parent: '%s-%s'", name, version);
        try {
            return projectAction.getProject(name, version);
        } catch (DependencyTrackException ex) {
            logger.error("Failed to find parent project with name ['%s-%s']. Check the update parent " +
                "your settings for this plugin and verify if a matching parent project exists in the " +
                "server.", name, version);
            throw ex;
        }
    }

    private String getBomLocation() {
        if (StringUtils.isNotBlank(bomLocation)) {
            return bomLocation;
        } else {
            String defaultLocation = mavenProject.getBasedir() + "/target/bom.xml";
            logger.debug("bomLocation not supplied so using: %s", defaultLocation);
            return defaultLocation;
        }
    }

    /*
     * Setters for dependency injection in tests
     */
    void setBomLocation(String bomLocation) {
        this.bomLocation = bomLocation;
        commonConfig.setBomLocation(bomLocation);
    }

    void setCommonConfig(CommonConfig commonConfig) {
        commonConfig.setMavenProject(this.mavenProject);
        this.commonConfig = commonConfig;
    }

    CommonConfig getCommonConfig() {
        return commonConfig;
    }

    void setMavenProject(MavenProject mp) {
        this.mavenProject = mp;
        commonConfig.setMavenProject(mp);
    }

    void setUpdateParent(boolean updateParent) {
        this.updateParent = updateParent;
        commonConfig.setUpdateParent(updateParent);
    }

    public String getParentUuid() { return commonConfig.getParentUuid(); }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
        commonConfig.setParentUuid(parentUuid);
    }

    void setParentName(String parentName) {
        this.parentName = parentName;
        commonConfig.setParentName(parentName);
    }

    void setParentVersion(String parentVersion) {
        this.parentVersion = parentVersion;
        commonConfig.setParentVersion(parentVersion);
    }

    void setLatest(boolean isLatest) {
        this.isLatest = isLatest;
        commonConfig.setLatest(isLatest);
    }

    void setProjectTags(Set<String> projectTags) {
        this.projectTags = projectTags;
        commonConfig.setProjectTags(projectTags);
    }
}
