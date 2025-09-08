package io.github.pmckeown.dependencytrack.finding.report;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import io.github.pmckeown.dependencytrack.report.TransformerFactoryProvider;
import javax.xml.XMLConstants;
import javax.xml.transform.TransformerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class FindingsReportHtmlReportWriterTest {

    @InjectMocks
    private FindingsReportHtmlReportWriter candidate;

    @Mock
    private TransformerFactoryProvider transformerFactoryProvider;

    @Mock
    private TransformerFactory transformerFactory;

    @Test
    public void thatSecureProcessingIsEnabledInTransformerFactoryToProtectFromXxeAttacks() throws Exception {
        doReturn(transformerFactory).when(transformerFactoryProvider).provide();

        candidate.getSecureTransformerFactory();

        verify(transformerFactory).setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    }
}
