package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Singleton
class HtmlReportWriter {

    private TransformerFactoryProvider transformerFactoryProvider;

    @Inject
    HtmlReportWriter(TransformerFactoryProvider transformerFactoryProvider) {
        this.transformerFactoryProvider = transformerFactoryProvider;
    }

    void write(File buildDirectory) throws DependencyTrackException {
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

    TransformerFactory getSecureTransformerFactory() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = transformerFactoryProvider.provide();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return transformerFactory;
    }

    private File getInputFile(File buildDirectory) {
        return new File(buildDirectory, FindingsReportConstants.XML_REPORT_FILENAME);
    }

    private File getOutputFile(File buildDirectory) {
        return new File(buildDirectory, FindingsReportConstants.HTML_REPORT_FILENAME);
    }

    private InputStream getStylesheetInputStream() {
        return HtmlReportWriter.class.getResourceAsStream(FindingsReportConstants.XSL_STYLESHEET_FILENAME);
    }

}
