package io.github.pmckeown.dependencytrack.upload;

import java.util.List;
import java.util.Set;
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

	private final String projectName;
	private final String projectVersion;
	private final boolean autoCreate;
	private final String base64EncodedBom;
	private final boolean isLatest;
	private final List<ProjectTag> projectTags;

	UploadBomRequest(final String projectName, final String projectVersion, final boolean autoCreate,
			final String base64EncodedBom, final boolean isLatest, final Set<String> projectTags) {
		this.projectName = projectName;
		this.projectVersion = projectVersion;
		this.autoCreate = autoCreate;
		this.base64EncodedBom = base64EncodedBom;
		this.isLatest = isLatest;
		if (projectTags == null) {
			this.projectTags = null;
		} else {
			this.projectTags = projectTags.stream().map(ProjectTag::new).collect(Collectors.toList());
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
