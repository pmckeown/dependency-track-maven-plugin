package io.github.pmckeown.dependencytrack.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public abstract class AbstractHtmlReportWriter {
    protected TransformerFactoryProvider transformerFactoryProvider;

    public AbstractHtmlReportWriter(TransformerFactoryProvider transformerFactoryProvider) {
        this.transformerFactoryProvider = transformerFactoryProvider;
    }

    public void write(File buildDirectory) throws DependencyTrackException {
        try {
            StreamSource stylesheet = new StreamSource(getStylesheetInputStream());
            StreamSource input = new StreamSource(getInputFile(buildDirectory));
            StreamResult output = new StreamResult(new FileOutputStream(getOutputFile(buildDirectory)));

            Transformer transformer = getSecureTransformerFactory().newTransformer(stylesheet);
            transformer.transform(input, output);
        } catch (Exception ex) {
            throw new DependencyTrackException("Error occurred when creating HTML report", ex);
        }
    }

    protected abstract File getInputFile(File buildDirectory);

    protected abstract File getOutputFile(File buildDirectory);

    protected abstract InputStream getStylesheetInputStream();

    public TransformerFactory getSecureTransformerFactory() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = transformerFactoryProvider.provide();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return transformerFactory;
    }
}
