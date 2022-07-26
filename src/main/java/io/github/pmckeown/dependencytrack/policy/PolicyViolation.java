package io.github.pmckeown.dependencytrack.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.pmckeown.dependencytrack.finding.Component;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class PolicyViolation {

    private PolicyCondition policyCondition;
    private Component component;

    @JsonCreator
    public PolicyViolation(@JsonProperty("component") Component component,
                           @JsonProperty("analysis") PolicyCondition policyCondition) {
        this.component = component;
        this.policyCondition = policyCondition;
    }

    @XmlElement
    public Component getComponent() {
        return component;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @XmlElement
    public PolicyCondition getPolicyCondition() {
        return policyCondition;
    }
}
