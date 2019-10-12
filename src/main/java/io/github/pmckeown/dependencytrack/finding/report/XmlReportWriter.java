package io.github.pmckeown.dependencytrack.finding.report;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

@Singleton
public class XmlReportWriter {

    private FindingsReportMarshallerService marshallerService;

    @Inject
    public XmlReportWriter(FindingsReportMarshallerService marshallerService) {
        this.marshallerService = marshallerService;
    }

    public void write(FindingsReport findingsReport) throws JAXBException {
        Marshaller marshaller = marshallerService.getMarshaller();
        File outputFile = getFile();
        marshaller.marshal(findingsReport, outputFile);
    }

    private File getFile() {
        File targetDir = new File(FindingsReportConstants.OUTPUT_DIRECTORY);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        return new File(FindingsReportConstants.XML_REPORT_FILENAME);
    }
}
