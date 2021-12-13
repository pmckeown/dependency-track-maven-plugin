# Testing dependency-track-maven-plugin
The project contauns a number of unit and in-memory integration tests that verify the codebase.  

However, it is also useful to be able to test the project against a real Dependency Track server.  

## Integrated Testing

### Running a Local Dependency Track
To run a Dependency Track server, the easiest option if you have Docker for Desktop installed is to use
the Docker Compose command from the Dependency Track project.

```shell script
curl --location --remote-name https://dependencytrack.org/docker-compose.yml
docker-compose up
```

### Running the Plugin against Dependency Track
In order run the plugin against a real Dependency Track, you will need to have the Dependency Track 
server running and an API Key configured.  You can find out how to do that on the Dependency Track 
[REST API](https://docs.dependencytrack.org/integrations/rest-api/) documentation page.

To easily run the plugin, you can use the `eat-your-own-dog-food` Maven Profile defined in the project POM
file.  You will need to export or define a couple of environment variables for the profile to work.

```shell script
export DEPENDENCY_TRACK_BASE_URL=http://localhost:8081 
export DEPENDENCY_TRACK_API_KEY=<YOUR_API_KEY> 
mvn clean install -DskipTests -Peat-your-own-dog-food
```

Notes:
- The project tests can be skipped for the profile executions
- The API backend port when using the `docker-compose up` from the Dependency Track project is by default `8081`  