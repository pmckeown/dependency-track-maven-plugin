package io.github.pmckeown.dependencytrack.policyviolation.report;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;

@Named
@Singleton
class PolicyViolationsReportMarshallerService {

    Marshaller getMarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PolicyViolationsReport.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;
    }
}
