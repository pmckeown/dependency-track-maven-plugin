package io.github.pmckeown.dependencytrack.finding.report;

import javax.inject.Singleton;
import javax.xml.XMLConstants;
import javax.xml.transform.TransformerFactory;

@Singleton
class TransformerFactoryProvider {

    TransformerFactory provide() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        // Issue 79 - Protect against XXE attacks
        // https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        return transformerFactory;
    }
}
