package io.github.pmckeown.dependencytrack.finding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.pmckeown.dependencytrack.suppressions.AbstractVulnerability;
import io.github.pmckeown.dependencytrack.suppressions.VulnerabilitySuppression;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Finding extends AbstractVulnerability {

    private Component component;
    private Vulnerability vulnerability;
    private Analysis analysis;

    @JsonCreator
    public Finding(@JsonProperty("component") Component component,
           @JsonProperty("vulnerability") Vulnerability vulnerability, @JsonProperty("analysis") Analysis analysis) {
        this.component = component;
        this.vulnerability = vulnerability;
        this.analysis = analysis;
    }

    @XmlElement
    public Component getComponent() {
        return component;
    }

    @XmlElement
    public Vulnerability getVulnerability() {
        return vulnerability;
    }

    @XmlElement
    public Analysis getAnalysis() {
        return analysis;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public String getVulnerabilityIdString() {
        return vulnerability.getSource() + ":" + vulnerability.getVulnId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (obj.getClass().equals(getClass())) {
            AbstractVulnerability other = (AbstractVulnerability) obj;
            return Objects.equals(getVulnerabilityIdString(), other.getVulnerabilityIdString()) ;
        } else return false;
    }

}
