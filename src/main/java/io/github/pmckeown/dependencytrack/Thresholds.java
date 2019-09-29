package io.github.pmckeown.dependencytrack;

public class Thresholds {

    private Integer critical;
    private Integer high;
    private Integer medium;
    private Integer low;

    public Thresholds() {
        this.critical = null;
        this.high = null;
        this.medium = null;
        this.low = null;
    }

    public Thresholds(Integer critical, Integer high, Integer medium, Integer low) {
        this.critical = critical;
        this.high = high;
        this.medium = medium;
        this.low = low;
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

    public boolean isEmpty() {
        return critical == null && high == null && medium == null && low == null;
    }
}
