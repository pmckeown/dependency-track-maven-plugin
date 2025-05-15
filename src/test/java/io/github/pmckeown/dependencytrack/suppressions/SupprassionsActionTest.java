package io.github.pmckeown.dependencytrack.suppressions;

import static io.github.pmckeown.dependencytrack.PollingConfig.TimeUnit.MILLIS;
import static io.github.pmckeown.dependencytrack.project.ProjectBuilder.aProject;
import static io.github.pmckeown.dependencytrack.upload.UploadBomResponseBuilder.anUploadBomResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.pmckeown.dependencytrack.CommonConfig;
import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.Poller;
import io.github.pmckeown.dependencytrack.PollingConfig;
import io.github.pmckeown.dependencytrack.Response;
import io.github.pmckeown.dependencytrack.project.Project;
import io.github.pmckeown.dependencytrack.upload.BomProcessingResponse;
import io.github.pmckeown.dependencytrack.upload.BomProcessingResponseBuilder;
import io.github.pmckeown.dependencytrack.upload.UploadBomAction;
import io.github.pmckeown.dependencytrack.upload.UploadBomRequest;
import io.github.pmckeown.dependencytrack.upload.UploadBomResponse;
import io.github.pmckeown.util.Logger;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SupprassionsActionTest {

    @InjectMocks
    private SuppressionsAction suppressionsAction;

    @Mock
    AnalysisClient analysisClient;

    @Mock
    private CommonConfig commonConfig;

    @Mock
    private Logger logger;

    private Project project;

    private List<Analysis> analysisList;

    @Before
    public void setUp() {
        project = aProject().build();
    }


}
