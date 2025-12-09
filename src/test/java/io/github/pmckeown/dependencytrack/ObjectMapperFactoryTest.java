package io.github.pmckeown.dependencytrack;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.pmckeown.dependencytrack.project.Project;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import kong.unirest.GenericType;
import kong.unirest.jackson.JacksonObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class ObjectMapperFactoryTest {

    @Test
    void thatUnknownPropertiesAreIgnoredWhenDeserializingJson() throws Exception {
        JacksonObjectMapper om = new JacksonObjectMapper(ObjectMapperFactory.relaxedObjectMapper());

        List<Project> projects = om.readValue(
                IOUtils.toString(
                        FileUtils.openInputStream(
                                new File("src/test/resources/__files/api/v1/project/get-all-projects.json")),
                        StandardCharsets.UTF_8),
                new GenericType<List<Project>>() {});

        assertNotNull(projects);
    }
}
