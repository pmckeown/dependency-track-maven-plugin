package io.github.pmckeown.dependencytrack.suppressions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Maven configuration for suppressions.
 *
 * @author Thomas Hucke
 */
public class Suppressions {

    private List<VulnerabilitySuppression> vulnerabilitySuppressions;

    public Suppressions() {
    }

    @JsonCreator
    public Suppressions(
        @JsonProperty("vulnerabilitySuppressions") List<VulnerabilitySuppression> vulnerabilitySuppressions
    ) {
        this.vulnerabilitySuppressions = vulnerabilitySuppressions;
    }

    @XmlElement(name = "vulnerabilitySuppressionList")
    public List<VulnerabilitySuppression> getVulnerabilitySuppressions() {
        return vulnerabilitySuppressions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vulnerabilitySuppressions);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Suppressions other = (Suppressions) obj;
        return Objects.equals(vulnerabilitySuppressions, other.vulnerabilitySuppressions);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public void setVulnerabilitySuppressions(List<VulnerabilitySuppression> vulnerabilitySuppressions) {
        this.vulnerabilitySuppressions = vulnerabilitySuppressions;
    }
}