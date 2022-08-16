package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import io.github.pmckeown.dependencytrack.finding.Severity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "findingsReport")
@XmlType(propOrder = {"policyApplied", "policyBreached", "critical", "high", "medium", "low", "unassigned"})
public class FindingsReport {

    private PolicyApplied policyApplied;
    private Boolean policyBreached;
    private List<Finding> findings;

    public FindingsReport() {
        // For JAXB
    }

    public FindingsReport(FindingThresholds findingThresholds, List<Finding> findings, boolean policyBreached) {
        this.policyApplied = new PolicyApplied(findingThresholds);
        this.findings = findings;
        this.policyBreached = policyBreached;
    }

    @XmlElement(name = "policyApplied")
    public PolicyApplied getPolicyApplied() {
        return policyApplied;
    }

    @XmlElement(name = "policyBreached")
    public boolean getPolicyBreached() {
        return policyBreached;
    }

    @XmlElement(name="critical")
    public FindingsWrapper getCritical() {
        return filterFindings(findings, Severity.CRITICAL);
    }

    @XmlElement(name="high")
    public FindingsWrapper getHigh() {
        return filterFindings(findings, Severity.HIGH);
    }

    @XmlElement(name="medium")
    public FindingsWrapper getMedium() {
        return filterFindings(findings, Severity.MEDIUM);
    }

    @XmlElement(name = "low")
    public FindingsWrapper getLow() {
        return filterFindings(findings, Severity.LOW);
    }

    @XmlElement(name = "unassigned")
    public FindingsWrapper getUnassigned() {
        return filterFindings(findings, Severity.UNASSIGNED);
    }

    private FindingsWrapper filterFindings(List<Finding> findings, Severity severity) {
        List<Finding> filteredFindings = findings.stream().filter(
                finding -> finding.getVulnerability().getSeverity() ==  severity).collect(Collectors.toList());
        return new FindingsWrapper(filteredFindings.size(), filteredFindings);
    }

    @XmlType(propOrder = { "info", "critical", "high", "medium", "low", "unassigned" }, name = "policyApplied")
    static class PolicyApplied {

        private static final String NO_POLICY_APPLIED_MESSAGE = "No policy was applied";
        private Integer critical;
        private Integer high;
        private Integer medium;
        private Integer low;
        private Integer unassigned;
        private String info;

        PolicyApplied(FindingThresholds findingThresholds) {
            if (findingThresholds != null) {
                if (findingThresholds.isEmpty()) {
                    this.info = NO_POLICY_APPLIED_MESSAGE;
                }
                this.critical = findingThresholds.getCritical();
                this.high = findingThresholds.getHigh();
                this.medium = findingThresholds.getMedium();
                this.low = findingThresholds.getLow();
                this.unassigned = findingThresholds.getUnassigned();
            }
        }

        @XmlAttribute(name = "info", required = false)
        public String getInfo() {
            return info;
        }

        @XmlElement(name = "maximumCriticalIssueCount", required = false)
        public Integer getCritical() {
            return critical;
        }

        @XmlElement(name = "maximumHighIssueCount", required = false)
        public Integer getHigh() {
            return high;
        }

        @XmlElement(name = "maximumMediumIssueCount", required = false)
        public Integer getMedium() {
            return medium;
        }

        @XmlElement(name = "maximumLowIssueCount", required = false)
        public Integer getLow() {
            return low;
        }

        @XmlElement(name = "maximumUnassignedIssueCount", required = false)
        public Integer getUnassigned() {
            return unassigned;
        }
    }
}
