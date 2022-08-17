package io.github.pmckeown.dependencytrack.policy.report;

import io.github.pmckeown.dependencytrack.report.AbstractHtmlReportWriter;
import io.github.pmckeown.dependencytrack.report.TransformerFactoryProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.InputStream;

@Singleton
public class PolicyViolationsHtmlReportWriter extends AbstractHtmlReportWriter {

    @Inject
    PolicyViolationsHtmlReportWriter(TransformerFactoryProvider transformerFactoryProvider) {
        super(transformerFactoryProvider);
    }

    @Override
    protected File getInputFile(File buildDirectory) {
        return new File(buildDirectory, PolicyViolationsReportConstants.XML_REPORT_FILENAME);
    }

    @Override
    protected File getOutputFile(File buildDirectory) {
        return new File(buildDirectory, PolicyViolationsReportConstants.HTML_REPORT_FILENAME);
    }

    @Override
    protected InputStream getStylesheetInputStream() {
        return PolicyViolationsHtmlReportWriter.class.getResourceAsStream(PolicyViolationsReportConstants.XSL_STYLESHEET_FILENAME);
    }

}
