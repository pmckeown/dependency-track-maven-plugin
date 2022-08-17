package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.finding.report.FindingsReportMarshallerService;
import io.github.pmckeown.dependencytrack.report.AbstractXmlReportWriter;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class PolicyViolationsXmlReportWriter extends AbstractXmlReportWriter {

    private FindingsReportMarshallerService findingsReportMarshallerService;

    @Inject
    public PolicyViolationsXmlReportWriter(FindingsReportMarshallerService findingsReportMarshallerService) {
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
        return new File(buildDirectory, PolicyViolationsReportConstants.XML_REPORT_FILENAME);
    }
}
