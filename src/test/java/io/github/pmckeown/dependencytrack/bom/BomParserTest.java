package io.github.pmckeown.dependencytrack.bom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.pmckeown.dependencytrack.project.ProjectInfo;
import io.github.pmckeown.util.Logger;
import java.io.File;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @ValueSource(strings = {"bom.xml", "bom_byteorder.xml"})
    void thatProjectInfoCanBeParsedFromBom(String file) {
        File bomFile = getBomLocation(file);
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

    @ParameterizedTest
    @ValueSource(strings = {"bom-without-metadata.xml", "bom-without-component.xml", "bom-1.1.xml"})
    void thatEmptyIsReturnedOnIncompleteBom(String file) {
        File bomFile = getBomLocation(file);
        assertThat(bomParser.getProjectInfo(bomFile).isPresent(), is(equalTo(false)));
    }

    @Test
    void thatParsingProjectInfoFailsWhenProvidedInvalidJsonFile() {
        File invalidFile = new File("target/test-classes/__files/api/v1/project/get-all-projects.json");
        assertThat(bomParser.getProjectInfo(invalidFile).isPresent(), is(equalTo(false)));
    }

    private File getBomLocation(String file) {
        URL bom = BomParser.class.getResource(file);
        assertNotNull(bom, "Missing " + file);
        return new File(bom.getFile());
    }
}
