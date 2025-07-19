package io.github.pmckeown.dependencytrack.policyviolation;

public class PolicyConditionBuilder {

    private Policy policy;
    private String subject = "Severity";
    private String operator = "==";
    private String value = "IS HIGH";

    private PolicyConditionBuilder() {
        // Use builder factory method
    }

    public static PolicyConditionBuilder aPolicyCondition() {
        return new PolicyConditionBuilder();
    }

    public PolicyConditionBuilder withSubject(String s) {
        this.subject = s;
        return this;
    }

    public PolicyConditionBuilder withOperator(String o) {
        this.operator = o;
        return this;
    }

    public PolicyConditionBuilder withValue(String v) {
        this.value = v;
        return this;
    }

    public PolicyConditionBuilder withPolicy(Policy policy) {
        this.policy = policy;
        return this;
    }

    public PolicyCondition build() {
        return new PolicyCondition(policy, subject, operator, value);
    }
}
