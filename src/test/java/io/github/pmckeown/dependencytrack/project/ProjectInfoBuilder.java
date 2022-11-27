package io.github.pmckeown.dependencytrack.project;

/**
 * Builder class for thr {@link ProjectInfo} object.
 *
 * Currently only supports the Group property.
 */
public class ProjectInfoBuilder {

    private String group = "org.apache";

    public static ProjectInfoBuilder aProjectInfo() {
        return new ProjectInfoBuilder();
    }

    public ProjectInfoBuilder withGroup(String group) {
        this.group = group;
        return this;
    }

    public ProjectInfo build() {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setGroup(this.group);
        return projectInfo;
    }
}
