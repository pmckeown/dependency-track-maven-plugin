package io.github.pmckeown.dependencytrack.finding.report;

import javax.inject.Singleton;
import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Singleton
class HtmlReportWriter {

    void write() throws IOException, TransformerException {
        StreamSource stylesheet = new StreamSource(getStylesheetFile());
        StreamSource input = new StreamSource(getInputFile());
        StreamResult output = new StreamResult(new FileOutputStream(getOutputFile()));

        Transformer transformer = getSecureTransformerFactory().newTransformer(stylesheet);
        transformer.transform(input, output);
    }

    private TransformerFactory getSecureTransformerFactory() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return transformerFactory;
    }

    private File getInputFile() {
        return new File(FindingsReportConstants.XML_REPORT_FILENAME);
    }

    private File getOutputFile() {
        return new File(FindingsReportConstants.HTML_REPORT_FILENAME);
    }

    private File getStylesheetFile() {
        return new File(FindingsReportConstants.XSL_STYLESHEET_FILENAME);
    }

}
