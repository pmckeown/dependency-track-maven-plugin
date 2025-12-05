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

@ExtendWith(MockitoExtension.class)
class FindingsReportHtmlReportWriterTest {

    @InjectMocks
    private FindingsReportHtmlReportWriter candidate;

    @Mock
    private TransformerFactoryProvider transformerFactoryProvider;

    @Mock
    private TransformerFactory transformerFactory;

    @Test
    void thatSecureProcessingIsEnabledInTransformerFactoryToProtectFromXxeAttacks() throws Exception {
        doReturn(transformerFactory).when(transformerFactoryProvider).provide();

        candidate.getSecureTransformerFactory();

        verify(transformerFactory).setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    }
}
