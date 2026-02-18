package io.github.pmckeown.dependencytrack.policyviolation.report;

import io.github.pmckeown.dependencytrack.report.AbstractXmlReportWriter;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PolicyViolationsXmlReportWriter extends AbstractXmlReportWriter {

    private PolicyViolationsReportMarshallerService policyViolationsReportMarshallerService;

    @Inject
    public PolicyViolationsXmlReportWriter(
            PolicyViolationsReportMarshallerService policyViolationsReportMarshallerService) {
        super();
        this.policyViolationsReportMarshallerService = policyViolationsReportMarshallerService;
    }

    @Override
    protected Marshaller getMarshaller() throws JAXBException {
        return this.policyViolationsReportMarshallerService.getMarshaller();
    }

    @Override
    protected File getFile(File buildDirectory) {
        if (buildDirectory != null && !buildDirectory.exists()) {
            buildDirectory.mkdir();
        }
        return new File(buildDirectory, PolicyViolationsReportConstants.XML_REPORT_FILENAME);
    }
}
