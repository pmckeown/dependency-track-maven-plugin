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

import javax.inject.Inject;

import static java.lang.String.format;

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

    @Parameter(defaultValue = "${project.parent.name}", property = "dependency-track.parentName")
    private String parentName;

    @Parameter(defaultValue = "${project.parent.version}", property = "dependency-track.parentVersion")
    private String parentVersion;

    private UploadBomAction uploadBomAction;

    private MetricsAction metricsAction;

    private ProjectAction projectAction;

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
        try {
            logger.info("Update Project Parent: %s", updateParent);

            Project parent = null;
            if (updateParent) {
                if (StringUtils.isEmpty(parentName)) {
                    throw new DependencyTrackException(
                            "Attempt to set parent with no default parent name and none provided");
                } else if (StringUtils.isEmpty(parentVersion)) {
                    throw new DependencyTrackException(
                            "Attempt to set parent with no default parent version and none provided");
                }
                logger.info("Parent Name: %s", parentName);
                logger.info("Parent Version: %s", parentVersion);
                getLog().info(format("Attempting to fetch project parent: %s-%s", parentName, parentVersion));
                parent = projectAction.getProject(parentName, parentVersion);

                if (parent == null) {
                    throw new DependencyTrackException(format("Server did not find project parent: %s-%s", parentName,
                            parentVersion));
                }
            }

            if (!uploadBomAction.upload(getBomLocation())) {
                handleFailure("Bom upload failed");
            }
            Project project = projectAction.getProject(projectName, projectVersion);

            UpdateRequest updateReq = new UpdateRequest();
            if (updateProjectInfo) updateReq.withBomLocation(getBomLocation());
            if (projectAction.updateRequired(updateReq.withParent(parent))) {
                if (!projectAction.updateProject(project, updateReq)) {
                    throw new DependencyTrackException("Failed to update project info");
                }
                project = projectAction.getProject(projectName, projectVersion);
            }

            metricsAction.refreshMetrics(project);
        } catch (DependencyTrackException ex) {
            handleFailure("Error occurred during upload", ex);
        }
    }

    public void updateParent(boolean updateParent) {
        this.updateParent = updateParent;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setParentVersion(String parentVersion) {
        this.parentVersion = parentVersion;
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
    }

    void setMavenProject(MavenProject mp) {
        this.mavenProject = mp;
    }

}
