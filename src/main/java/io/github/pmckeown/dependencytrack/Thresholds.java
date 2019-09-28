package io.github.pmckeown.dependencytrack;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "critical", "high", "medium", "low" })
public class Thresholds {

    private Integer critical;
    private Integer high;
    private Integer medium;
    private Integer low;

    public Thresholds() {
        // Initialises metric levels to all zero
    }

    public Thresholds(int critical, int high, int medium, int low) {
        this.critical = critical;
        this.high = high;
        this.medium = medium;
        this.low = low;
    }

    @XmlElement(name = "maximumCriticalIssueCount")
    public Integer getCritical() {
        return critical;
    }

    @XmlElement(name = "maximumHighIssueCount")
    public Integer getHigh() {
        return high;
    }

    @XmlElement(name = "maximumMediumIssueCount")
    public Integer getMedium() {
        return medium;
    }

    @XmlElement(name = "maximumLowIssueCount")
    public Integer getLow() {
        return low;
    }
}
