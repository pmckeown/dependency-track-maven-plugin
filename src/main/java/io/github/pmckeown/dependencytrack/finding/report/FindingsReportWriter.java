package io.github.pmckeown.dependencytrack.finding.report;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

@Singleton
public class FindingsReportWriter {

    private FindingsReportMarshallerService marshallerService;

    @Inject
    public FindingsReportWriter(FindingsReportMarshallerService marshallerService) {
        this.marshallerService = marshallerService;
    }

    public void write(FindingsReport findingsReport) throws JAXBException {
        Marshaller marshaller = marshallerService.getMarshaller();
        File outputFile = getFile();
        marshaller.marshal(findingsReport, outputFile);
    }

    private File getFile() {
        File targetDir = new File("target");
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        return new File("target/dependency-track-findings.xml");
    }
}
