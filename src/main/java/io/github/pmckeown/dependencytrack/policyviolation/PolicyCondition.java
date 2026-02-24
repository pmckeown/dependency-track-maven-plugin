package io.github.pmckeown.dependencytrack.policyviolation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PolicyCondition {

    private Policy policy;
    private String subject;
    private String operator;
    private String value;

    @JsonCreator
    public PolicyCondition(
            @JsonProperty("policy") Policy policy,
            @JsonProperty("subject") String subject,
            @JsonProperty("operator") String operator,
            @JsonProperty("value") String value) {
        this.policy = policy;
        this.subject = subject;
        this.operator = operator;
        this.value = value;
    }

    @XmlElement
    public Policy getPolicy() {
        return policy;
    }

    @XmlElement
    public String getSubject() {
        return subject;
    }

    @XmlElement
    public String getOperator() {
        return operator;
    }

    @XmlElement
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
