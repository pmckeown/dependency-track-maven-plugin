package io.github.pmckeown.dependencytrack.finding;

import java.util.ArrayList;
import java.util.List;

public class FindingListBuilder {

    private List<Finding> findings = new ArrayList<>();

    public static FindingListBuilder aListOfFindings() {
        return new FindingListBuilder();
    }

    public FindingListBuilder withFinding(FindingBuilder findingBuilder) {
        this.findings.add(findingBuilder.build());
        return this;
    }

    public List<Finding> build() {
        return this.findings;
    }
}
