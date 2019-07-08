[![Build Status](https://travis-ci.com/pmckeown/dependency-track-maven-plugin.svg?branch=master)](https://travis-ci.com/pmckeown/dependency-track-maven-plugin)

# dependency-track-maven-plugin
Maven plugin to integrate with a [Dependency Track](https://dependencytrack.org/) server to submit dependency manifests 
and gather project metrics.

#### Usage
This maven plugin provides various functions relating to Dependency Track, from uploading the Bill of Material, to 
checking for 3rd party dependencies with vulnerabilities in your Maven POM.  Common configuration can be provided 
in the `pluginManagement` section of your POM to avoid repetition.

#### Plugin Configuration
```xml
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

The following options are common to all goals and can be declared centrally in the plugin management definition 
of this plugin:

|Property              |Required|Default Value        |
|----------------------|--------|---------------------|
|dependencyTrackBaseUrl|true    |N/A                  |
|apiKey                |true    |N/A                  |
|projectName           |false   |${project.artifactId}|
|projectVersion        |false   |${project.version}   |
|failOnError           |false   |false                |


## Features

### Upload Bill of Material
Upload a Bill of Material (BOM) to a Dependency Track server.  By default this uploads the bom.xml and creates (or 
updates if already present) a project on the Dependency Track server with the name and version that map to the current
maven project artifactId and version.  Set the 

#### POM Usage
Binds by default to the Verify Phase in the Maven lifecycle in line with 
[cyclonedx-maven-plugin](https://github.com/CycloneDX/cyclonedx-maven-plugin).

#### Direct Usage
```
mvn dependency-track:upload-bom
```

#### Direct Usage with Overrides
```
mvn dependency-track:upload-bom -Ddependency-track.projectName=arbitrary-name -Ddependency-track.projectVersion=99.99
```

#### Dependencies
Needs to be used in conjunction with the [cyclonedx-maven-plugin](https://github.com/CycloneDX/cyclonedx-maven-plugin)
which will generate a bom.xml file from the declared and transitive dependencies in your POM.

If both the cyclonedx-maven-plugin and this plugin are run in the same phase, the cyclonedx-maven-plugin should be declared
first in your POM to ensure that it runs first.

#### Configuration

|Property      |Required|Default Value        |
|--------------|--------|---------------------|
|bomLocation   |false   |target/bom.xml       |

### Get Inherited Risk Score
Get the Inherited Risk Score from the Dependency Track server for the current project or any arbitrary project.
The Risk Score per vulnerability tells you about a specific vulnerability in a dependency in your application and how 
vulnerable you are to exploit and how bad the impact could be.  The Inherited Risk Score provides a summation of those
Risk Scores into a single value.

The Inherited Risk Score as a single value is useful if you only want to have coarse grained checks in your build, such 
failing on a score greater than zero. 

If the `inheritedRiskScoreThreshold` parameter is set, then the build will fail if the Inherited Risk Score is **greater 
than** the threshold.

#### POM Usage
Binds by default to the Verify Phase in the Maven lifecycle.  This goal should be run after the `upload-bom` goal with
enough time for the server to compute the new score if it has changed.

Ideally `upload-bom` would run during the validate phase and `score` in the verify phase.

#### Direct Usage
```
mvn dependency-track:score
```

#### Dependencies
Depends on a project existing in the Dependency Track server that matches the current project artifactId and version or
whatever overridden values that are supplied.

#### Configuration

|Property                   |Required|Default Value|
|---------------------------|--------|-------------|
|inheritedRiskScoreThreshold|false   |N/A          |