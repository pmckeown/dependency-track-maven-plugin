package io.github.pmckeown.dependencytrack.builders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pmckeown.dependencytrack.project.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectListBuilder {

    private List<Project> projects = new ArrayList<>();

    public static ProjectListBuilder aListOfProjects() {
        return new ProjectListBuilder();
    }

    public ProjectListBuilder withProject(ProjectBuilder projectBuilder) {
        this.projects.add(projectBuilder.build());
        return this;
    }

    public String asJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this.projects);
    }
}
