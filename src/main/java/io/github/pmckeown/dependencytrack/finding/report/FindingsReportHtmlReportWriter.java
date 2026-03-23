package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.report.AbstractHtmlReportWriter;
import io.github.pmckeown.dependencytrack.report.TransformerFactoryProvider;
import java.io.File;
import java.io.InputStream;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;

@Named
@Singleton
public class FindingsReportHtmlReportWriter extends AbstractHtmlReportWriter {

    @Inject
    FindingsReportHtmlReportWriter(TransformerFactoryProvider transformerFactoryProvider) {
        super(transformerFactoryProvider);
    }

    @Override
    protected File getInputFile(File buildDirectory) {
        return new File(buildDirectory, FindingsReportConstants.XML_REPORT_FILENAME);
    }

    @Override
    protected File getOutputFile(File buildDirectory) {
        return new File(buildDirectory, FindingsReportConstants.HTML_REPORT_FILENAME);
    }

    @Override
    protected InputStream getStylesheetInputStream() {
        return FindingsReportHtmlReportWriter.class.getResourceAsStream(
                FindingsReportConstants.XSL_STYLESHEET_FILENAME);
    }
}
