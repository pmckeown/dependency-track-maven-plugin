package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.policy.PolicyViolation;
import io.github.pmckeown.dependencytrack.report.Report;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "policyViolationReport")
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
    public PolicyViolationsWrapper getPolicyViolationWrapper() {
        return new PolicyViolationsWrapper(policyViolations.size(), policyViolations);
    }
}
