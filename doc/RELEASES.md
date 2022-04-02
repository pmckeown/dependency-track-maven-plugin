# Releasing the Dependency Track Maven Plugin

To initiate a new release, raise a Pull Request to the main branch which contains the requested change(s) and sets the 
POM version number to the next release version.

This project follows semantic versioning principles laid out at [SemVer](https://semver.org/).  As such please follow 
these guidelines when setting the project version number:
* Major - Reserved for major releases as defined by the project team or when changes are made to the plugin interface 
that are not backwards compatible with the previous version.
* Minor - Backwards compatible changes to the interface, e.g. a new goal, a new configuration
option.
* Patch - Implementation changes or bug fixes that have no impact on the interface.  

# Release Steps

The following steps should be followed when creating a release of this plugin.

**Note these should be performed on the _main_ branch only where that branch is up to date with the remote**

1. `mvn clean deploy -Prelease`
2. `git tag -a -s <VERSION_NUMBER> -m 'Release version <VERSION_NUMBER> - <BRIEF_DESCRIPTION_OF_CHANGES>'`
3. `git push origin --tags`

After these steps remember to set the next SNAPSHOT version release number and commit that to the `main` branch.

1. change pom.xml version number
2. `git commit -am 'Bumping to next SNAPSHOT release version'`
3. `git push`

# Notes

The release process signs the published artifacts as a requirement for publishing to the Sonatype OSS Repository Hosting
(OSSRH) servers.  Follow the instructions [here](https://blog.sonatype.com/2010/01/how-to-generate-pgp-signatures-with-maven/) 
on how to generate, publish and use a pgp key for use when signing the release artifacts.  
