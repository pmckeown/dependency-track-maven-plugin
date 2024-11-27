package io.github.pmckeown.dependencytrack.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import io.github.pmckeown.dependencytrack.AbstractDependencyTrackMojo;
import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.FindingsAction;
import io.github.pmckeown.dependencytrack.finding.Suppression;
import io.github.pmckeown.dependencytrack.metrics.MetricsAction;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.project.ProjectAction;
import io.github.pmckeown.dependencytrack.project.UpdateRequest;
import io.github.pmckeown.util.Logger;

/**
 * Provides the capability to upload a Bill of Material (BOM) to your Dependency Track server. The BOM may any format
 * supported by your Dependency Track server, has only been tested with the output from the
 * <a href="https://github.com/CycloneDX/cyclonedx-maven-plugin">cyclonedx-maven-plugin</a> in the
 * <a href="https://cyclonedx.org/">CycloneDX</a> format Specific configuration options are:
 * <ol>
 * <li>bomLocation</li>
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

	@Parameter(property = "dependency-track.parentVersion")
	private String parentVersion;

	@Parameter(property = "dependency-track.isLatest", defaultValue = "false")
	private boolean isLatest;

	@Parameter(property = "dependency-track.projectTags")
	private Set<String> projectTags;

	@Parameter(property = "dependency-track.suppressions")
	private List<Suppression> suppressions = new ArrayList<>();

	private final UploadBomAction uploadBomAction;

	private final MetricsAction metricsAction;

	private final ProjectAction projectAction;

	private final FindingsAction findingsAction;

	@Inject
	public UploadBomMojo(final UploadBomAction uploadBomAction, final MetricsAction metricsAction,
			final ProjectAction projectAction, final FindingsAction findingsAction, final CommonConfig commonConfig,
			final Logger logger) {
		super(commonConfig, logger);
		this.uploadBomAction = uploadBomAction;
		this.metricsAction = metricsAction;
		this.projectAction = projectAction;
		this.findingsAction = findingsAction;
	}

	@Override
	public void performAction() throws MojoExecutionException, MojoFailureException {
		logger.info("Update Project Parent : %s", updateParent);

		try {
			if (!uploadBomAction.upload(getBomLocation(), isLatest, projectTags)) {
				handleFailure("Bom upload failed");
			}
			final Project project = projectAction.getProject(projectName, projectVersion);

			final UpdateRequest updateReq = new UpdateRequest();
			if (updateProjectInfo) {
				updateReq.withBomLocation(getBomLocation());
			}
			if (updateParent) {
				updateReq.withParent(getProjectParent(parentName, parentVersion));
			}
			if (updateProjectInfo || updateParent) {
				final boolean projectUpdated = projectAction.updateProject(project, updateReq, projectTags);
				if (!projectUpdated) {
					logger.error("Failed to update project info");
					throw new DependencyTrackException("Failed to update project info");
				}
			}
			if (!suppressions.isEmpty()) {
				findingsAction.suppressFindings(project, suppressions);
			}

			metricsAction.refreshMetrics(project);
		} catch (final DependencyTrackException ex) {
			handleFailure("Error occurred during upload", ex);
		}
	}

	private Project getProjectParent(final String parentName, final String parentVersion)
			throws DependencyTrackException {
		if (StringUtils.isEmpty(parentName)) {
			logger.error("Parent update requested but no parent found in parent maven project or provided in config");
			throw new DependencyTrackException("No parent found.");
		} else {
			logger.info("Attempting to fetch project parent: '%s-%s'", parentName, parentVersion);

			try {
				return projectAction.getProject(parentName, parentVersion);
			} catch (final DependencyTrackException ex) {
				logger.error("Failed to find parent project with name ['%s-%s']. Check the update parent "
						+ "your settings for this plugin and verify if a matching parent project exists in the "
						+ "server.", parentName, parentVersion);
				throw ex;
			}
		}
	}

	private String getBomLocation() {
		if (StringUtils.isNotBlank(bomLocation)) {
			return bomLocation;
		} else {
			final String defaultLocation = mavenProject.getBasedir() + "/target/bom.xml";
			logger.debug("bomLocation not supplied so using: %s", defaultLocation);
			return defaultLocation;
		}
	}

	/*
	 * Setters for dependency injection in tests
	 */
	void setBomLocation(final String bomLocation) {
		this.bomLocation = bomLocation;
	}

	void setMavenProject(final MavenProject mp) {
		mavenProject = mp;
	}

	void setUpdateParent(final boolean updateParent) {
		this.updateParent = updateParent;
	}

	void setParentName(final String parentName) {
		this.parentName = parentName;
	}

	void setParentVersion(final String parentVersion) {
		this.parentVersion = parentVersion;
	}

	void setLatest(final boolean isLatest) {
		this.isLatest = isLatest;
	}

	void setProjectTags(final Set<String> projectTags) {
		this.projectTags = projectTags;
	}
}
