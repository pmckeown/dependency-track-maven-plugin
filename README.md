[![Build Status](https://travis-ci.com/pmckeown/dependency-track-maven-plugin.svg?branch=master)](https://travis-ci.com/pmckeown/dependency-track-maven-plugin)

# dependency-track-maven-plugin
Maven plugin to integrate with a [Dependency Track](https://dependencytrack.org/) server to submit dependency manifests and gather project metrics.

#### Usage
This maven plugin provides various functions relating to Dependency Track, from uploading the Bill of Material, to 
checking for 3rd party dependencies with vulnerabilities in your Maven POM.  Common confguration should be provided 
in the `pluginManagement` section of your POM to avoid repetition of common configuration.

#### Plugin Configuration
```
<pluginManagement>
    <plugins>
        <plugin>
            <groupId>io.github.pmckeown</groupId>
            <artifactId>dependency-track-maven-plugin</artifactId>
            <version>${dependency-track-maven-plugin.version}</version>
            <configuration>
                <dependencyTrackBaseUrl>http://localhost:8080</dependencyTrackBaseUrl>
                <apiKey>API_KEY</apiKey>
            </configuration>
        </plugin>
    </plugins>
</pluginManagement>
```

#### Dependency Track Configuration
Your Dependency Track server must be configured with an `Automation` team whose API Key should be provided
in the `apiKey` configuration parameter to this plugin.

The Automation team in Dependency Track needs the following permissions:
* BOM_UPLOAD
* PORTFOLIO_MANAGEMENT
* PROJECT_CREATION_UPLOAD
* SCAN_UPLOAD
* VIEW_PORTFOLIO
* VULNERABILITY_ANALYSIS

|Property              |Required|Default Value|
|----------------------|--------|-------------|
|dependencyTrackBaseUrl|true    |N/A          |
|apiKey                |true    |N/A          |  


## Features

### Upload Bill of Material
Upload a Bill of Material (BOM) to a Dependency Track server.

#### Usage
Binds by default to the Verify Phase in the Maven lifecycle in line with 
[cyclonedx-maven-plugin](https://github.com/CycloneDX/cyclonedx-maven-plugin).    

#### Dependencies
Needs to be used in conjunction with the [cyclonedx-maven-plugin](https://github.com/CycloneDX/cyclonedx-maven-plugin)
which will generate a bom.xml file from the declared and transitive dependencies in your POM.

If both the cyclonedx-maven-plugin and this plugin are run in the same phase, the cyclonedx-maven-plugin should be declared
first in your POM to ensure that it runs first.

#### Configuration

|Property      |Required|Default Value        |
|--------------|--------|---------------------|
|bomLocation   |false   |target/bom.xml       |
|projectName   |false   |${project.artifactId}|
|projectVersion|false   |${project.version}   |