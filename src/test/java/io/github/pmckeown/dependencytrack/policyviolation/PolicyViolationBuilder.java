package io.github.pmckeown.dependencytrack.policyviolation;

import io.github.pmckeown.dependencytrack.finding.Component;
import io.github.pmckeown.dependencytrack.finding.ComponentBuilder;

import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.policyviolation.PolicyConditionBuilder.aPolicyCondition;

public class PolicyViolationBuilder {

    private PolicyCondition policyCondition;
    private Component component;
    private String type;

    private PolicyViolationBuilder() {
        // Use builder factory method
    }

    public static PolicyViolationBuilder aPolicyViolation() {
        return new PolicyViolationBuilder();
    }

    public static PolicyViolationBuilder aDefaultPolicyViolation() {
        PolicyViolationBuilder policyViolationBuilder = new PolicyViolationBuilder();
        policyViolationBuilder.withComponent(aComponent());
        policyViolationBuilder.withPolicyCondition(aPolicyCondition());
        policyViolationBuilder.withType("SECURITY");
        return policyViolationBuilder;
    }

    public PolicyViolationBuilder withComponent(ComponentBuilder componentBuilder) {
        this.component = componentBuilder.build();
        return this;
    }

    public PolicyViolationBuilder withPolicyCondition(PolicyConditionBuilder policyConditionBuilder) {
        this.policyCondition = policyConditionBuilder.build();
        return this;
    }

    public PolicyViolationBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public PolicyViolation build() {
        return new PolicyViolation(component, policyCondition, type);
    }
}
