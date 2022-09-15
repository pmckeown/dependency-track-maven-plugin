package io.github.pmckeown.dependencytrack.policyviolation.report;

import io.github.pmckeown.dependencytrack.policyviolation.PolicyViolation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

public class PolicyViolationsWrapper {

    private int count;
    private List<PolicyViolation> policyViolations;

    public PolicyViolationsWrapper(int count, List<PolicyViolation> policyViolations) {
        this.count = count;
        this.policyViolations = policyViolations;
    }

    @XmlElement(name = "count")
    public int getCount() {
        return count;
    }

    @XmlElementWrapper(name = "policyViolations")
    @XmlElement(name = "policyViolation")
    public List<PolicyViolation> getPolicyViolations() {
        return policyViolations;
    }
}
