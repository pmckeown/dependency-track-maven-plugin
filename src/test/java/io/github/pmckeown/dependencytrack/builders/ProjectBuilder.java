package io.github.pmckeown.dependencytrack.builders;

import io.github.pmckeown.dependencytrack.metrics.Metrics;
import io.github.pmckeown.dependencytrack.project.Project;

public class ProjectBuilder {

    private String uuid;
    private String name;
    private String version;
    private Metrics metrics;

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

    Project build() {
        return new Project(uuid, name, version, metrics);
    }
}
