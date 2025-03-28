This is a maven project with sub-modules. The submodule refers to the project's main project.

The main maven project configures the dependency track plugin to be executed.
As the submodule uses the main project as parent the child also executes the dependency track plugin.

The project has a ever changing version so that it will always try to upload a new
version to Dependency Track.
