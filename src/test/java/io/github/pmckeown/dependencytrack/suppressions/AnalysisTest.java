package io.github.pmckeown.dependencytrack.suppressions;

import static io.github.pmckeown.dependencytrack.suppressions.AnalysisBuilder.fixType1Analysis;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pmckeown.dependencytrack.AbstractDependencyTrackIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnalysisTest extends AbstractDependencyTrackIntegrationTest {

    @Test
    public void thatDeserializingUsingJsonCreatorIsCorrect() throws JsonProcessingException {
        String json =
            "{\"analysisDetails\":\"Suppresses a given CVE for a dependency with the given sha1 until the current date is"
                + " 1 Jan 2020 or beyond.\",\"analysisJustification\":\"NOT_SET\",\"analysisResponse\":\"NOT_SET\","
                + "\"analysisState\":\"IN_TRIAGE\",\"component\":\"07bc5a6b-4e33-4dc4-b65d-294338e414b5\",\"isSuppressed\":true,"
                + "\"project\":\"ca7acccc-9e72-4844-8b37-e5b4d0f08c30\",\"suppressed\":true,"
                + "\"vulnerability\":\"52e0aa0f-e657-4a30-b0b1-f8f2326ae6da\"}";

        Analysis analysis = new ObjectMapper().readValue(json, Analysis.class);
        assertThat(analysis.getProjectUuid(), is(equalTo("ca7acccc-9e72-4844-8b37-e5b4d0f08c30")));
        assertThat(analysis.getComponentUuid(), is(equalTo("07bc5a6b-4e33-4dc4-b65d-294338e414b5")));
        assertThat(analysis.getVulnerabilityUuid(), is(equalTo("52e0aa0f-e657-4a30-b0b1-f8f2326ae6da")));
    }

    @Test
    public void thatSerializingUsingJsonCreatorContainsKeyElements() {
        Analysis analysis = fixType1Analysis().build();
        String json = analysis.toString();
        assertThat(json, is(not(nullValue())));
        assertThat(json.contains("projectUuid"), is(equalTo(false)));
        assertThat(json.contains("project"), is(equalTo(true)));
        assertThat(json.contains("vulnerabilityUuid"), is(equalTo(false)));
        assertThat(json.contains("vulnerability"), is(equalTo(true)));
        assertThat(json.contains("componentUuid"), is(equalTo(false)));
        assertThat(json.contains("component"), is(equalTo(true)));
    }
}
