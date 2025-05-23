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

    public static ComponentBuilder fixMalkavineComponent() {
        return new ComponentBuilder()
            .withUuid("4fa9fd01-55bc-4810-bb44-a7cba242ea6b")
            .withName("malkavine-component")
            .withGroup("eu.malkavine")
            .withVersion("1.1.11");
    }

    public static ComponentBuilder fixAeroxeifeinComponent() {
        return new ComponentBuilder()
            .withUuid("4fa9fd01-55bc-4810-bb44-a7cba242ea6b")
            .withName("aeroxeifein-component")
            .withGroup("dev.aeroxeifein")
            .withVersion("2.2.22");
    }

    public ComponentBuilder withUuid(String n) {
        this.uuid = uuid;
        return this;
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
