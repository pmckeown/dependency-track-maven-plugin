package io.github.pmckeown.dependencytrack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE);
        if (critical != null) {
            sb.append("critical", critical);
        }
        if (high != null) {
            sb.append("high", high);
        }
        if (medium != null) {
            sb.append("medium", medium);
        }
        if (low != null) {
            sb.append("low", low);
        }
        if (unassigned != null) {
            sb.append("unassigned", unassigned);
        }
        return sb.toString();
    }
}
