package io.github.pmckeown.dependencytrack.finding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Component {

    private String uuid;
    private String name;
    private String group;
    private String version;

    @JsonCreator
    public Component(@JsonProperty("uuid") String uuid, @JsonProperty("name") String name,
             @JsonProperty("group") String group, @JsonProperty("version") String version) {
        this.uuid = uuid;
        this.name = name;
        this.group = group;
        this.version = version;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
