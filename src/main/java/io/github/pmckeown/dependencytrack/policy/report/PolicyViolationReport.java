package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.policy.PolicyViolation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "policyViolationReport")
@XmlType(propOrder = {"policyViolations"})
public class PolicyViolationReport {

    private List<PolicyViolation> policyViolations;

    public PolicyViolationReport() {
        // For JAXB
    }

    public PolicyViolationReport(List<PolicyViolation> policyViolations) {
        this.policyViolations = policyViolations;
    }

    @XmlElement(name = "policyViolations")
    public PolicyViolationsWrapper getPolicyViolationWrapper() {
        return new PolicyViolationsWrapper(policyViolations.size(), policyViolations);
    }
}
