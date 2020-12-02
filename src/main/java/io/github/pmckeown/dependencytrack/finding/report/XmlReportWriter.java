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

    void write(File buildDirectory, FindingsReport findingsReport) throws DependencyTrackException {
        try {
            Marshaller marshaller = marshallerService.getMarshaller();
            File outputFile = getFile(buildDirectory);
            marshaller.marshal(findingsReport, outputFile);
        } catch (JAXBException ex) {
            throw new DependencyTrackException("Error occurred while generating XML report", ex);
        }
    }

    private File getFile(File buildDirectory) {
        if (buildDirectory != null && !buildDirectory.exists()) {
            buildDirectory.mkdir();
        }
        return new File(buildDirectory, FindingsReportConstants.XML_REPORT_FILENAME);
    }
}
