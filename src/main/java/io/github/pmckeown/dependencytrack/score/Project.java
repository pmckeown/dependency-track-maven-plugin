package io.github.pmckeown.dependencytrack.score;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.pmckeown.dependencytrack.metrics.Metrics;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Model class for the Project object
 *
 * @author Paul McKeown
 */
public class Project {

    private String uuid;
    private String name;
    private String version;
    private Metrics metrics;

    @JsonCreator
    public Project(@JsonProperty("uuid") String uuid, @JsonProperty("name") String name,
               @JsonProperty("version") String version, @JsonProperty("metrics") Metrics metrics) {
        this.uuid = uuid;
        this.name = name;
        this.version = version;
        this.metrics = metrics;
    }

    public String getUuid() {
        return uuid;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
