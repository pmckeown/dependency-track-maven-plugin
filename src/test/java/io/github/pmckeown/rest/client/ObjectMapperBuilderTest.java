package io.github.pmckeown.rest.client;

import io.github.pmckeown.rest.model.Project;
import kong.unirest.GenericType;
import kong.unirest.JacksonObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @author Paul McKeown
 */
public class ObjectMapperBuilderTest {

    @Test
    public void thatUnknownPropertiesAreIgnoredWhenDeserializingJson() throws Exception {
        JacksonObjectMapper om = new JacksonObjectMapper(ObjectMapperBuilder.relaxedObjectMapper());

        List<Project> projects = om.readValue(IOUtils.toString(FileUtils.openInputStream(
                new File("src/test/resources/__files/api/v1/project/get-all-projects.json"))),
                new GenericType<List<Project>>(){});

        assertNotNull(projects);

    }
}
