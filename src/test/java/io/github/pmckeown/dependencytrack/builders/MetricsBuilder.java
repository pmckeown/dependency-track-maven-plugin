package io.github.pmckeown.dependencytrack.builders;

import io.github.pmckeown.dependencytrack.metrics.Metrics;

public class MetricsBuilder {

    private int inheritedRiskScore;
    private int critical;
    private int high;
    private int medium;
    private int low;
    private int unassigned;
    private int vulnerabilities;
    private int vulnerableComponents;
    private int components;
    private int suppressed;
    private int findingsTotal;
    private int findingsAudited;
    private int findingsUnaudited;
    private long firstOccurrence;
    private long lastOccurrence;

    private MetricsBuilder() {
    }

    public static MetricsBuilder aDefaultMetrics() {
        return new MetricsBuilder();
    }

    public MetricsBuilder withInheritedRiskScore(int irs) {
        this.inheritedRiskScore = irs;
        return this;
    }

    public MetricsBuilder withCritical(int c) {
        this.critical = c;
        return this;
    }

    public MetricsBuilder withHigh(int h) {
        this.high = h;
        return this;
    }

    public MetricsBuilder withMedium(int m) {
        this.medium = m;
        return this;
    }

    public MetricsBuilder withLow(int l) {
        this.low = l;
        return this;
    }

    public MetricsBuilder withUnassigned(int ua) {
        this.unassigned = ua;
        return this;
    }

    public MetricsBuilder withVulnerabilities(int v) {
        this.vulnerabilities = v;
        return this;
    }

    public MetricsBuilder withVulnerableComponents(int vc) {
        this.vulnerableComponents = vc;
        return this;
    }

    public MetricsBuilder withComponents(int c) {
        this.components = c;
        return this;
    }

    public MetricsBuilder withSuppressed(int s) {
        this.suppressed = s;
        return this;
    }

    public MetricsBuilder withFindingsTotal(int ft) {
        this.findingsTotal = ft;
        return this;
    }

    public MetricsBuilder withFindingsAudited(int fa) {
        this.findingsAudited = fa;
        return this;
    }

    public MetricsBuilder withFindingsUnaudited(int fu) {
        this.findingsUnaudited = fu;
        return this;
    }

    public MetricsBuilder withFirstOccurrence(long fo) {
        this.firstOccurrence = fo;
        return this;
    }

    public MetricsBuilder withLastOccurrence(long lo) {
        this.lastOccurrence = lo;
        return this;
    }

    public Metrics build() {
        return new Metrics(inheritedRiskScore, critical, high, medium, low, unassigned, vulnerabilities,
                vulnerableComponents, components, suppressed, findingsTotal, findingsAudited, findingsUnaudited,
                firstOccurrence, lastOccurrence);
    }
}
