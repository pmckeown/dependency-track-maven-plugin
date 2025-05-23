package io.github.pmckeown.dependencytrack.suppressions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven configuration for suppressions.
 *
 * @author Thomas Hucke
 */
public class Suppressions {

    @Parameter(name = "vulnerabilitySuppressions")
    private List<VulnerabilitySuppression> vulnerabilitySuppressions = Collections.emptyList();

    @Parameter(name = "strictMode", property = "dependency-track.suppressions.strictMode", defaultValue = "false")
    private boolean strictMode;

    public Suppressions() { }

    @JsonCreator
    public Suppressions(
        @JsonProperty("vulnerabilitySuppressions") List<VulnerabilitySuppression> vulnerabilitySuppressions,
        @JsonProperty("strictMode") boolean strictMode
    ) {
        this.vulnerabilitySuppressions = vulnerabilitySuppressions;
        this.strictMode = strictMode;
    }

    @XmlElement(name = "vulnerabilitySuppression")
    public List<VulnerabilitySuppression> getVulnerabilitySuppressions() {
        return vulnerabilitySuppressions;
    }

    @XmlElement(name = "strictMode")
    public boolean isStrictMode() {
        return strictMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vulnerabilitySuppressions, strictMode);
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
        return Objects.equals(vulnerabilitySuppressions, other.vulnerabilitySuppressions) &&
            Objects.equals(strictMode, other.strictMode);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public void setVulnerabilitySuppressions(List<VulnerabilitySuppression> vulnerabilitySuppressions) {
        this.vulnerabilitySuppressions = vulnerabilitySuppressions;
    }
}