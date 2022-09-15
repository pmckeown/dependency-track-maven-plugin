package io.github.pmckeown.dependencytrack.policyviolation;

import java.util.ArrayList;
import java.util.List;

public class PolicyViolationListBuilder {

    private List<PolicyViolation> policyViolations = new ArrayList<>();

    public static PolicyViolationListBuilder aListOfPolicyViolations() {
        return new PolicyViolationListBuilder();
    }

    public PolicyViolationListBuilder withPolicyViolation(PolicyViolationBuilder policyViolationBuilder) {
        this.policyViolations.add(policyViolationBuilder.build());
        return this;
    }

    public List<PolicyViolation> build() {
        return this.policyViolations;
    }
}
