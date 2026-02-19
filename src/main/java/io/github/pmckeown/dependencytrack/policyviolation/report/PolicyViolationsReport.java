package io.github.pmckeown.dependencytrack.policyviolation.report;

import io.github.pmckeown.dependencytrack.policyviolation.PolicyViolation;
import io.github.pmckeown.dependencytrack.report.Report;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "policyViolationsReport")
@XmlType(propOrder = {"policyViolations"})
public class PolicyViolationsReport implements Report {

    private List<PolicyViolation> policyViolations;

    public PolicyViolationsReport() {
        // For JAXB
    }

    public PolicyViolationsReport(List<PolicyViolation> policyViolations) {
        this.policyViolations = policyViolations;
    }

    @XmlElement(name = "policyViolations")
    public PolicyViolationsWrapper getPolicyViolations() {
        return new PolicyViolationsWrapper(policyViolations.size(), policyViolations);
    }
}
