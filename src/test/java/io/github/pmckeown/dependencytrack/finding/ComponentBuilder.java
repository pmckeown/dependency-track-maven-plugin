package io.github.pmckeown.dependencytrack.finding;

import java.util.UUID;

public class ComponentBuilder {

    private String uuid = UUID.randomUUID().toString();
    private String name = "password-printer";
    private String group = "com.nefarious";
    private String version = "1.0.0";

    private ComponentBuilder() {
        // Use builder factory method
    }

    public static ComponentBuilder aComponent() {
        return new ComponentBuilder();
    }

    public ComponentBuilder withName(String n) {
        this.name = n;
        return this;
    }

    public ComponentBuilder withGroup(String g) {
        this.group = g;
        return this;
    }

    public ComponentBuilder withVersion(String v) {
        this.version = v;
        return this;
    }

    public Component build() {
        return new Component(uuid, name, group, version);
    }
}
