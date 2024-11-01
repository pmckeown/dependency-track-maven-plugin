package io.github.pmckeown.dependencytrack.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.pmckeown.dependencytrack.Item;
import io.github.pmckeown.dependencytrack.metrics.Metrics;
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

    @JsonCreator
    public Project(@JsonProperty("uuid") String uuid,
                   @JsonProperty("name") String name,
                   @JsonProperty("version") String version,
                   @JsonProperty("metrics") Metrics metrics,
                   @JsonProperty("isLatest") boolean isLatest) {
        super(uuid);
        this.name = name;
        this.version = version;
        this.metrics = metrics;
        this.isLatest = isLatest;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
