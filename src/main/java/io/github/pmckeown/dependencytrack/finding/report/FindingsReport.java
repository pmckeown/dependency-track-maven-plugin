package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import io.github.pmckeown.dependencytrack.finding.Severity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "findingsReport")
@XmlType(propOrder = {"policyApplied", "critical", "high", "medium", "low", "error"})
public class FindingsReport {

    private FindingThresholds policyApplied;
    private List<Finding> findings;
    private String error;

    public FindingsReport() {
        // For JAXB
    }

    public FindingsReport(FindingThresholds policyApplied, List<Finding> findings) {
        this.policyApplied = policyApplied;
        this.findings = findings;
        this.error = null;
    }

    public FindingsReport(String error) {
        this.error = error;
        this.policyApplied = null;
        this.findings = null;
    }

    @XmlElement(name = "policyApplied")
    public FindingThresholds getPolicyApplied() {
        return policyApplied;
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

    @XmlElement(name = "error")
    public String getError() {
        return error;
    }

    private FindingsWrapper filterFindings(List<Finding> findings, Severity severity) {
        List<Finding> filteredFindings = findings.stream().filter(
                finding -> finding.getVulnerability().getSeverity() ==  severity).collect(Collectors.toList());
        return new FindingsWrapper(filteredFindings.size(), filteredFindings);
    }
}
