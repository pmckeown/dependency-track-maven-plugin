package io.github.pmckeown.dependencytrack.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;

public class Policy {

    private String name;
    private String violationState;

    @JsonCreator
    public Policy(@JsonProperty("name") String name, @JsonProperty("violationState") String violationState) {
        this.name = name;
        this.violationState = violationState;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    @XmlElement
    public String getViolationState() {
        return violationState;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
