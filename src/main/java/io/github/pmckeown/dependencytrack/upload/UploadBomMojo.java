package io.github.pmckeown.dependencytrack.upload;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.ModuleConfig;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.dependencytrack.project.UpdateRequest;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.api.Lifecycle.Phase;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.annotations.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the capability to upload a Bill of Material (BOM) to your Dependency Track server.
 *
 * <p>The BOM may any format supported by your Dependency Track server, has only been tested with
 * the output from the <a
 * href="https://github.com/CycloneDX/cyclonedx-maven-plugin">cyclonedx-maven-plugin</a> in the <a
 * href="https://cyclonedx.org/">CycloneDX</a> format
 *
 * <p>Specific configuration options are:
 *
 * <ol>
 *   <li>bomLocation
 * </ol>
 *
 * @author Paul McKeown
 */
@Mojo(name = "upload-bom", defaultPhase = Phase.VERIFY)
public class UploadBomMojo extends AbstractDependencyTrackMojo {
    private static final Logger LOG = LoggerFactory.getLogger(UploadBomMojo.class);

    @Parameter(property = "dependency-track.bomLocation")
    private String bomLocation;

    @Inject
    private org.apache.maven.api.Project mavenProject;

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

    @Parameter(property = "dependency-track.isLatest")
    private Boolean isLatest;

    @Parameter(property = "dependency-track.projectTags")
    private Set<String> projectTags;

    @Parameter(property = "dependency-track.uploadWithPut", defaultValue = "true")
    private boolean uploadWithPut = true;

    @Inject
    private UploadBomAction uploadBomAction;

    @Inject
    private MetricsAction metricsAction;

    @Inject
    private ProjectAction projectAction;

    @Override
    public void performAction() throws MojoExecutionException, MojoFailureException {
        enrichConfig();
        LOG.info("Update Project Parent : {}", moduleConfig.getUpdateParent());

        try {
            if (!uploadBomAction.upload(moduleConfig, uploadWithPut)) {
                handleFailure("Bom upload failed");
            }
            Project project = projectAction.getProject(moduleConfig);

            UpdateRequest updateReq = new UpdateRequest();
            if (updateProjectInfo) {
                updateReq.withBomLocation(getBomLocation());
            }
            if (updateParent) {
                updateReq.withParent(getProjectParent(moduleConfig));
            }
            if (updateProjectInfo || updateParent) {
                boolean projectUpdated = projectAction.updateProject(project, updateReq, projectTags);
                if (!projectUpdated) {
                    LOG.error("Failed to update project info");
                    throw new DependencyTrackException("Failed to update project info");
                }
            }

            metricsAction.refreshMetrics(project);
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred during upload", ex);
        }
    }

    private void enrichConfig() {
        this.moduleConfig.setBomLocation(getBomLocation());
        this.moduleConfig.setMavenProject(mavenProject);
        this.moduleConfig.setUpdateProjectInfo(updateProjectInfo);
        this.moduleConfig.setUpdateParent(updateParent);
        this.moduleConfig.setParentUuid(parentUuid);
        this.moduleConfig.setParentName(parentName);
        this.moduleConfig.setParentVersion(parentVersion);
        this.moduleConfig.setLatest(isLatest);
        this.moduleConfig.setProjectTags(projectTags);
    }

    private Project getProjectParent(ModuleConfig moduleConfig) throws DependencyTrackException {
        if (StringUtils.isBlank(moduleConfig.getParentName()) && StringUtils.isBlank(moduleConfig.getParentUuid())) {
            LOG.error("Parent update requested but no parent found in parent maven project or provided in config");
            throw new DependencyTrackException("No parent configured.");
        } else {
            if (StringUtils.isBlank(moduleConfig.getParentUuid()))
                return getProjectParentByNameAndVersion(moduleConfig.getParentName(), moduleConfig.getParentVersion());
            else return getProjectParentByUuid(moduleConfig.getParentUuid());
        }
    }

    private Project getProjectParentByUuid(String uuid) throws DependencyTrackException {
        LOG.info("Attempting to fetch project parent: '{}'", uuid);
        try {
            return projectAction.getProject(uuid);
        } catch (DependencyTrackException ex) {
            LOG.error(
                    "Failed to find parent project with UUID ['%s']. Check the update parent "
                            + "your settings for this plugin and verify if a matching parent project exists in the "
                            + "server.",
                    uuid);
            throw ex;
        }
    }

    private Project getProjectParentByNameAndVersion(String name, String version) throws DependencyTrackException {
        LOG.info("Attempting to fetch project parent: '{}-{}'", name, version);
        try {
            return projectAction.getProject(name, version);
        } catch (DependencyTrackException ex) {
            LOG.error(
                    "Failed to find parent project with name ['%s-%s']. Check the update parent "
                            + "your settings for this plugin and verify if a matching parent project exists in the "
                            + "server.",
                    name, version);
            throw ex;
        }
    }

    private String getBomLocation() {
        if (StringUtils.isNotBlank(bomLocation)) {
            return bomLocation;
        } else {
            String defaultLocation = mavenProject.getBasedir() + "/target/bom.xml";
            LOG.debug("bomLocation not supplied so using: {}", defaultLocation);
            return defaultLocation;
        }
    }

    /*
     * Setters for dependency injection in tests
     */
    void setBomLocation(String bomLocation) {
        this.bomLocation = bomLocation;
        moduleConfig.setBomLocation(bomLocation);
    }

    void setCommonConfig(CommonConfig commonConfig) {
        moduleConfig.setMavenProject(this.mavenProject);
        this.commonConfig = commonConfig;
    }

    CommonConfig getCommonConfig() {
        return commonConfig;
    }

    void setModuleConfig(ModuleConfig moduleConfig) {
        moduleConfig.setMavenProject(this.mavenProject);
        this.moduleConfig = moduleConfig;
    }

    ModuleConfig getModuleConfig() {
        return moduleConfig;
    }

    void setMavenProject(org.apache.maven.api.Project mp) {
        this.mavenProject = mp;
        moduleConfig.setMavenProject(mp);
    }

    void setUpdateParent(boolean updateParent) {
        this.updateParent = updateParent;
        moduleConfig.setUpdateParent(updateParent);
    }

    public String getParentUuid() {
        return moduleConfig.getParentUuid();
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
        moduleConfig.setParentUuid(parentUuid);
    }

    void setParentName(String parentName) {
        this.parentName = parentName;
        moduleConfig.setParentName(parentName);
    }

    void setParentVersion(String parentVersion) {
        this.parentVersion = parentVersion;
        moduleConfig.setParentVersion(parentVersion);
    }

    void setLatest(boolean isLatest) {
        this.isLatest = isLatest;
        moduleConfig.setLatest(isLatest);
    }

    void setProjectTags(Set<String> projectTags) {
        this.projectTags = projectTags;
        moduleConfig.setProjectTags(projectTags);
    }

    void setUploadWithPut(boolean uploadWithPut) {
        this.uploadWithPut = uploadWithPut;
    }
}
