package io.github.pmckeown.dependencytrack.project;

public class UpdateRequest {
    private String bomLocation;
    private Project parent;

    public boolean hasBomLocation() {
        return bomLocation != null;
    }

    public String getBomLocation() {
        return bomLocation;
    }

    public UpdateRequest withBomLocation(String bomLocation) {
        this.bomLocation = bomLocation;
        return this;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public Project getParent() {
        return parent;
    }

    public UpdateRequest withParent(Project parent) {
        this.parent = parent;
        return this;
    }
}
