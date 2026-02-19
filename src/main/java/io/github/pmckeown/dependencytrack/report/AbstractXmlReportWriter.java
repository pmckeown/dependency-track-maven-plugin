package io.github.pmckeown.dependencytrack.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;

public abstract class AbstractXmlReportWriter {

    public void write(File buildDirectory, Report report) throws DependencyTrackException {
        try {
            File outputFile = getFile(buildDirectory);
            getMarshaller().marshal(report, outputFile);
        } catch (JAXBException ex) {
            throw new DependencyTrackException("Error occurred while generating XML report", ex);
        }
    }

    protected abstract Marshaller getMarshaller() throws JAXBException;

    protected abstract File getFile(File buildDirectory);
}
