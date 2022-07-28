package io.github.pmckeown.dependencytrack.policy;

import javax.inject.Singleton;

@Singleton
public class PolicyConfig {

    private String policyName;
    private String violationState;
    private String riskType;
    private Integer threshold;

    public PolicyConfig() {
        this.policyName = null;
        this.violationState = null;
        this.riskType = null;
        this.threshold = null;
    }

    public PolicyConfig(String policyName, String violationState, String riskType, Integer threshold) {
        this.policyName = policyName;
        this.violationState = violationState;
        this.riskType = riskType;
        this.threshold = threshold;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getViolationState() {
        return violationState;
    }

    public void setViolationState(String violationState) {
        this.violationState = violationState;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}
