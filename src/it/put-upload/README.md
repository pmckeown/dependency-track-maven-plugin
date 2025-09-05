This test project will upload the SBOM to Dependency Track using the PUT API,
which was the default up to version 1.10.2.

The POST BOM API uses a multipart/form-data body instead of JSON. This API is
less limited on the BOM size, and also plays nicer with Web Application Firewalls.

https://github.com/pmckeown/dependency-track-maven-plugin/issues/454
