package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.report.AbstractXmlReportWriter;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class FindingsReportXmlReportWriter extends AbstractXmlReportWriter {

    private FindingsReportMarshallerService findingsReportMarshallerService;

    @Inject
    public FindingsReportXmlReportWriter(FindingsReportMarshallerService findingsReportMarshallerService) {
        super();
        this.findingsReportMarshallerService = findingsReportMarshallerService;
    }

    @Override
    protected Marshaller getMarshaller() throws JAXBException {
        return this.findingsReportMarshallerService.getMarshaller();
    }

    @Override
    protected File getFile(File buildDirectory) {
        if (buildDirectory != null && !buildDirectory.exists()) {
            buildDirectory.mkdir();
        }
        return new File(buildDirectory, FindingsReportConstants.XML_REPORT_FILENAME);
    }
}
