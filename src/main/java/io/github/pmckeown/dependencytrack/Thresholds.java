package io.github.pmckeown.dependencytrack;

public class Thresholds {

    private Integer critical;
    private Integer high;
    private Integer medium;
    private Integer low;
    private Integer unassigned;

    public Thresholds() {
        this.critical = null;
        this.high = null;
        this.medium = null;
        this.low = null;
        this.unassigned = null;
    }

    public Thresholds(Integer critical, Integer high, Integer medium, Integer low, Integer unassigned) {
        this.critical = critical;
        this.high = high;
        this.medium = medium;
        this.low = low;
        this.unassigned = unassigned;
    }

    public Integer getCritical() {
        return critical;
    }

    public Integer getHigh() {
        return high;
    }

    public Integer getMedium() {
        return medium;
    }

    public Integer getLow() {
        return low;
    }

    public Integer getUnassigned() {
        return unassigned;
    }

    public boolean isEmpty() {
        return critical == null && high == null && medium == null && low == null && unassigned == null;
    }
}
