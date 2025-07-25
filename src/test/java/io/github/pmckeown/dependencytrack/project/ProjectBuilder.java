package io.github.pmckeown.dependencytrack.project;

import io.github.pmckeown.dependencytrack.metrics.Metrics;
import io.github.pmckeown.dependencytrack.metrics.MetricsBuilder;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProjectBuilder {

    private String uuid = UUID.randomUUID().toString();
    private String name = "test-project";
    private String version = "2.0.0";
    private Metrics metrics;
    private boolean isLatest;
    private List<ProjectTag> tags;

    public static ProjectBuilder aProject() {
        return new ProjectBuilder();
    }

    public ProjectBuilder withUuid(String u) {
        this.uuid = u;
        return this;
    }

    public ProjectBuilder withName(String n) {
        this.name = n;
        return this;
    }

    public ProjectBuilder withVersion(String v) {
        this.version = v;
        return this;
    }

    public ProjectBuilder withMetrics(MetricsBuilder metricsBuilder) {
        this.metrics = metricsBuilder.build();
        return this;
    }

    public ProjectBuilder withIsLatest(boolean isLatest) {
        this.isLatest = isLatest;
        return this;
    }

    public ProjectBuilder withTags(Set<String> tags) {
        this.tags = tags.stream().map(ProjectTag::new).collect(Collectors.toList());
        return this;
    }

    public Project build() {
        return new Project(uuid, name, version, metrics, isLatest, tags);
    }
}
