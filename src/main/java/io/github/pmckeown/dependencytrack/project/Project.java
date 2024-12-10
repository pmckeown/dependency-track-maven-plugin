package io.github.pmckeown.dependencytrack.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.pmckeown.dependencytrack.Item;
import io.github.pmckeown.dependencytrack.metrics.Metrics;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Model class for the Project object
 *
 * @author Paul McKeown
 */
public class Project extends Item {
    private String name;
    private String version;
    private Metrics metrics;
    private boolean isLatest;
    private List<ProjectTag> tags;

    @JsonCreator
    public Project(@JsonProperty("uuid") String uuid,
                   @JsonProperty("name") String name,
                   @JsonProperty("version") String version,
                   @JsonProperty("metrics") Metrics metrics,
                   @JsonProperty("isLatest") boolean isLatest,
                   @JsonProperty("tags") List<ProjectTag> tags) {
        super(uuid);
        this.name = name;
        this.version = version;
        this.metrics = metrics;
        this.isLatest = isLatest;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public boolean isLatest() {
        return isLatest;
    }

    public List<ProjectTag> getTags() {
        return tags;
    }
}
