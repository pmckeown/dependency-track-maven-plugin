package io.github.pmckeown.dependencytrack.bom;

import io.github.pmckeown.dependencytrack.project.ProjectInfo;
import io.github.pmckeown.util.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BomParserTest {

    @InjectMocks
    private BomParser bomParser;

    @Mock
    private Logger logger;

    @Test
    public void thatProjectInfoCanBeParsedFromBom() {
        File bomFile = new File(BomParserTest.class.getResource("bom.xml").getFile());
        ProjectInfo info = bomParser.getProjectInfo(bomFile).get();
        assertThat(info.getGroup(), is(equalTo("io.github.pmckeown")));
        assertThat(info.getDescription(), is(equalTo("Maven plugin to integrate with a Dependency Track server to " +
                "submit dependency manifests and gather project metrics.")));
        assertThat(info.getPurl(), is(equalTo("pkg:maven/io.github.pmckeown/dependency-track-maven-plugin@1.2.1-" +
                "SNAPSHOT?type=maven-plugin")));
        assertThat(info.getClassifier(), is(equalTo("LIBRARY")));
    }
    @Test
    public void thatProjectInfoCanBeParsedFromBomWithByteOrder() {
        File bomFile = new File(BomParserTest.class.getResource("bom_byteorder.xml").getFile());
        ProjectInfo info = bomParser.getProjectInfo(bomFile).get();
        assertThat(info.getGroup(), is(equalTo("io.github.pmckeown")));
        assertThat(info.getDescription(), is(equalTo("Maven plugin to integrate with a Dependency Track server to " +
                "submit dependency manifests and gather project metrics.")));
        assertThat(info.getPurl(), is(equalTo("pkg:maven/io.github.pmckeown/dependency-track-maven-plugin@1.2.1-" +
                "SNAPSHOT?type=maven-plugin")));
        assertThat(info.getClassifier(), is(equalTo("LIBRARY")));
    }

    @Test
    public void thatEmptyIsReturnedWhenMissingBomFile() {
        assertThat(bomParser.getProjectInfo(new File("no-such-file")).isPresent(), is(equalTo(false)));
    }

    @Test
    public void thatEmptyIsReturnedWhenBomHasNoMetadata() {
        File bomFile = new File(BomParserTest.class.getResource("bom-without-metadata.xml").getFile());
        assertThat(bomParser.getProjectInfo(bomFile).isPresent(), is(equalTo(false)));
    }

    @Test
    public void thatEmptyIsReturnedWhenBomHasNoMetadataComponent() {
        File bomFile = new File(BomParserTest.class.getResource("bom-without-component.xml").getFile());
        assertThat(bomParser.getProjectInfo(bomFile).isPresent(), is(equalTo(false)));
    }

    @Test
    public void thatParsingProjectInfoFromOldBomVersionReturnsNoProjectInfo() {
        File bomFile = new File(BomParserTest.class.getResource("bom-1.1.xml").getFile());
        assertThat(bomParser.getProjectInfo(bomFile).isPresent(), is(equalTo(false)));
    }

    @Test
    public void thatParsingProjectInfoFailsWhenProvidedInvalidJsonFile() {
        File invalidFile = new File("target/test-classes/__files/api/v1/project/get-all-projects.json");
        assertThat(bomParser.getProjectInfo(invalidFile).isPresent(), is(equalTo(false)));
    }
}
