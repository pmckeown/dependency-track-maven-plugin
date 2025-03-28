This is a maven project with sub-modules. The submodule refers to the project's main project.

The main maven project configures the dependency track plugin to be executed.
As the submodule uses the main project as parent the child also executes the dependency track plugin.

This configuration is slightly different from the simple-module-project as the submodule
explicitly defines the `parentVersion` in the plugin configuration.
This shows that the explicit parent configuration does not fail due to issue #441.

The project has a ever changing version so that it will always try to upload a new
version to Dependency Track.