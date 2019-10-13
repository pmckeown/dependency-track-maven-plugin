package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

@Singleton
class XmlReportWriter {

    private FindingsReportMarshallerService marshallerService;

    @Inject
    XmlReportWriter(FindingsReportMarshallerService marshallerService) {
        this.marshallerService = marshallerService;
    }

    void write(FindingsReport findingsReport) throws DependencyTrackException {
        try {
            Marshaller marshaller = marshallerService.getMarshaller();
            File outputFile = getFile();
            marshaller.marshal(findingsReport, outputFile);
        } catch (JAXBException ex) {
            throw new DependencyTrackException("Error occurred while generating XML report", ex);
        }
    }

    private File getFile() {
        File targetDir = new File(FindingsReportConstants.OUTPUT_DIRECTORY);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        return new File(FindingsReportConstants.XML_REPORT_FILENAME);
    }
}
