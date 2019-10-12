[![Build Status](https://travis-ci.com/pmckeown/dependency-track-maven-plugin.svg?branch=master)](https://travis-ci.com/pmckeown/dependency-track-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.pmckeown/dependency-track-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.pmckeown/dependency-track-maven-plugin)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=io.github.pmckeown%3Adependency-track-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=io.github.pmckeown%3Adependency-track-maven-plugin)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=io.github.pmckeown%3Adependency-track-maven-plugin&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=io.github.pmckeown%3Adependency-track-maven-plugin)

# dependency-track-maven-plugin
Maven plugin to integrate with a [Dependency-Track](https://dependencytrack.org/) server to submit dependency manifests 
and gather project metrics.

#### Usage
This maven plugin provides various functions relating to Dependency-Track, from uploading the Bill of Material, to 
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

#### Polling Configuration
The plugin is configured to poll for results from the Dependency-Track server by default.  The polling configuration by 
be modified by modifying the `pollingConfig` element in the plugin configuration.  The available options are:

|Property |Required |Default Value |Support Values  |
|---------|---------|--------------|----------------|
|enabled  |false    |true          |                |
|pause    |false    |1             |                |
|attempts |false    |20            |                |
|timeUnit |false    |SECONDS       |MILLIS / SECONDS|

**XML Configuration Example**
```xml
<configuration>
    <pollingConfig>
        <enabled>true</enabled>
        <pause>500</pause>
        <attempts>40</attempts>
        <timeUnit>MILLIS</timeUnit>
    </pollingConfig>
</configuration>
```


#### Dependency-Track Configuration
Your Dependency-Track server must be configured with an `Automation` team whose API Key should be provided
in the `apiKey` configuration parameter to this plugin.

The Automation team in Dependency-Track needs the following permissions:
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
Upload a Bill of Material (BOM) to a Dependency-Track server.  By default this uploads the bom.xml and creates (or 
updates if already present) a project on the Dependency-Track server with the name and version that map to the current
maven project artifactId and version.  Set the projectName or projectVersion properties if you want to override the
project name or version. 

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

By default this goal will poll the Dependency-Track server after a BOM upload to ensure that it has been analysed 
before continuing with plugin execution.  This can be disabled by setting the `waitUntilBomProcessingComplete` 
parameter to `false`.

|Property                      |Required|Default Value |
|------------------------------|--------|--------------|
|bomLocation                   |false   |target/bom.xml|
|waitUntilBomProcessingComplete|false   |true          |

### Get Project Findings
After a BOM upload, the best way to determine if there are any vulnerabilities is to use the `findings` goal which is 
usable immediately after an upload.  Other goals, such as `metrics` and `score` pull down information from the
Dependency-Track server that is analysed asynchronously and as such may not be available when invoked immediately after 
a BOM upload.
  
The `findings` goal prints out some details of all of the current issues found in the scan, including: component 
details, vulnerability description and suppression status.

The `findings` goal can be configured to fail the build if the number of findings in a given category are higher than 
the threshold set for that category.

#### POM Usage
Binds by default to the Verify Phase in the Maven lifecycle in line with 
[cyclonedx-maven-plugin](https://github.com/CycloneDX/cyclonedx-maven-plugin).

#### Direct Usage
```
mvn dependency-track:findings
```

#### Direct Usage with Overrides
```
mvn dependency-track:findings -Ddependency-track.projectName=arbitrary-name -Ddependency-track.projectVersion=99.99
```

#### Dependencies
Depends on a project existing in the Dependency-Track server that matches the current project artifactId and version or
whatever overridden values that are supplied.

#### Configuration

|Property                  |Required|Default Value|Description                                                                                           |
|--------------------------|--------|-------------|------------------------------------------------------------------------------------------------------|
|findingThresholds         |false   |N/A          |If present with no child elements, any issues found in any category will cause the build to fail      |
|findingThresholds.critical|false   |0            |The build will fail if the issue count is higher than the configured threshold value for this category|
|findingThresholds.high    |false   |0            |The build will fail if the issue count is higher than the configured threshold value for this category|
|findingThresholds.medium  |false   |0            |The build will fail if the issue count is higher than the configured threshold value for this category|
|findingThresholds.low     |false   |0            |The build will fail if the issue count is higher than the configured threshold value for this category|

#### Examples
The following configuration will cause the build to fail if there are any critical or high issues found, more than 5 
medium issues or more than 10 low issues. 
```xml
<findingThresholds>
    <critical>0</critical>
    <high>0</high>
    <medium>5</medium>
    <low>10</low>
</findingThresholds>
```
You can enable the build to fail on any issues in any category by using the following configuration:
```xml
<findingThresholds />
```

### Get Inherited Risk Score
Get the Inherited Risk Score from the Dependency-Track server for the current project or any arbitrary project.
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
Depends on a project existing in the Dependency-Track server that matches the current project artifactId and version or
whatever overridden values that are supplied.

#### Configuration

|Property                   |Required|Default Value|
|---------------------------|--------|-------------|
|inheritedRiskScoreThreshold|false   |N/A          |

### Get Metrics
Get and print all metrics from the Dependency-Track server for the current project or any arbitrary project.  Optionally
define thresholds for each category of issue found to allow finer grained control over build failure.

#### POM Usage
Binds by default to the Verify Phase in the Maven lifecycle.  This goal should be run after the `upload-bom` goal with
enough time for the server to compute the new score if it has changed.

Ideally `upload-bom` would run during the validate phase and `metrics` in the verify phase.

#### Direct Usage
```
mvn dependency-track:metrics
```

#### Dependencies
Depends on a project existing in the Dependency-Track server that matches the current project artifactId and version or
whatever overridden values that are supplied.

#### Configuration

|Property                  |Required|Default Value|Description                                                                                           |
|--------------------------|--------|-------------|------------------------------------------------------------------------------------------------------|
|metricsThresholds         |false   |N/A          |If present with no child elements, any issues found in any category will cause the build to fail      |
|metricsThresholds.critical|false   |0            |The build will fail if the issue count is higher than the configured threshold value for this category|
|metricsThresholds.high    |false   |0            |The build will fail if the issue count is higher than the configured threshold value for this category|
|metricsThresholds.medium  |false   |0            |The build will fail if the issue count is higher than the configured threshold value for this category|
|metricsThresholds.low     |false   |0            |The build will fail if the issue count is higher than the configured threshold value for this category|

#### Examples
The following configuration will cause the build to fail if there are any critical or high issues found, more than 5 
medium issues or more than 10 low issues. 
```xml
<metricsThresholds>
    <critical>0</critical>
    <high>0</high>
    <medium>5</medium>
    <low>10</low>
</metricsThresholds>
```
You can enable the build to fail on any issues in any category by using the following configuration:
```xml
<metricsThresholds />
```

### Delete Project
Delete the current or any arbitrary project from the Dependency-Track server.

#### POM Usage
Does not bind by default to any Phase in the Maven lifecycle.  This goal can be run independently any time to delete a 
project.

Expected usage is for temporary scans of short-lived branch code that need to be cleaned up from the server once the 
score has been determined.

#### Direct Usage
```
mvn dependency-track:delete-project
```

#### Dependencies
Depends on a project existing in the Dependency-Track server that matches the current project artifactId and version or
whatever overridden values that are supplied.

#### Configuration
See common configuration above