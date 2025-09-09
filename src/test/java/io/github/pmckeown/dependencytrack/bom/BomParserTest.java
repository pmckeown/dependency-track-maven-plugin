package io.github.pmckeown.dependencytrack.bom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.github.pmckeown.dependencytrack.project.ProjectInfo;
import io.github.pmckeown.util.Logger;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
class BomParserTest {

    @InjectMocks
    private BomParser bomParser;

    @Mock
    private Logger logger;

    @Test
    void thatProjectInfoCanBeParsedFromBom() {
        File bomFile = new File(BomParserTest.class.getResource("bom.xml").getFile());
        ProjectInfo info = bomParser.getProjectInfo(bomFile).get();
        assertThat(info.getGroup(), is(equalTo("io.github.pmckeown")));
        assertThat(
                info.getDescription(),
                is(equalTo("Maven plugin to integrate with a Dependency Track server to "
                        + "submit dependency manifests and gather project metrics.")));
        assertThat(
                info.getPurl(),
                is(equalTo("pkg:maven/io.github.pmckeown/dependency-track-maven-plugin@1.2.1-"
                        + "SNAPSHOT?type=maven-plugin")));
        assertThat(info.getClassifier(), is(equalTo("LIBRARY")));
    }

    @Test
    void thatProjectInfoCanBeParsedFromBomWithByteOrder() {
        File bomFile =
                new File(BomParserTest.class.getResource("bom_byteorder.xml").getFile());
        ProjectInfo info = bomParser.getProjectInfo(bomFile).get();
        assertThat(info.getGroup(), is(equalTo("io.github.pmckeown")));
        assertThat(
                info.getDescription(),
                is(equalTo("Maven plugin to integrate with a Dependency Track server to "
                        + "submit dependency manifests and gather project metrics.")));
        assertThat(
                info.getPurl(),
                is(equalTo("pkg:maven/io.github.pmckeown/dependency-track-maven-plugin@1.2.1-"
                        + "SNAPSHOT?type=maven-plugin")));
        assertThat(info.getClassifier(), is(equalTo("LIBRARY")));
    }

    @Test
    void thatEmptyIsReturnedWhenMissingBomFile() {
        assertThat(bomParser.getProjectInfo(new File("no-such-file")).isPresent(), is(equalTo(false)));
    }

    @Test
    void thatEmptyIsReturnedWhenBomHasNoMetadata() {
        File bomFile = new File(
                BomParserTest.class.getResource("bom-without-metadata.xml").getFile());
        assertThat(bomParser.getProjectInfo(bomFile).isPresent(), is(equalTo(false)));
    }

    @Test
    void thatEmptyIsReturnedWhenBomHasNoMetadataComponent() {
        File bomFile = new File(
                BomParserTest.class.getResource("bom-without-component.xml").getFile());
        assertThat(bomParser.getProjectInfo(bomFile).isPresent(), is(equalTo(false)));
    }

    @Test
    void thatParsingProjectInfoFromOldBomVersionReturnsNoProjectInfo() {
        File bomFile = new File(BomParserTest.class.getResource("bom-1.1.xml").getFile());
        assertThat(bomParser.getProjectInfo(bomFile).isPresent(), is(equalTo(false)));
    }

    @Test
    void thatParsingProjectInfoFailsWhenProvidedInvalidJsonFile() {
        File invalidFile = new File("target/test-classes/__files/api/v1/project/get-all-projects.json");
        assertThat(bomParser.getProjectInfo(invalidFile).isPresent(), is(equalTo(false)));
    }
}
