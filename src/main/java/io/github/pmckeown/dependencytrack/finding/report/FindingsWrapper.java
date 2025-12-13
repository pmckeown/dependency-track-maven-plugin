package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.finding.Finding;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class FindingsWrapper {

    private int count;
    private List<Finding> findings;

    public FindingsWrapper(int count, List<Finding> findings) {
        this.count = count;
        this.findings = findings;
    }

    @XmlElement(name = "count")
    public int getCount() {
        return count;
    }

    @XmlElementWrapper(name = "findings")
    @XmlElement(name = "finding")
    public List<Finding> getFindings() {
        return findings;
    }
}
