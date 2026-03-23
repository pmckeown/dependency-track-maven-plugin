package io.github.pmckeown.dependencytrack.report;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerFactory;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.di.Singleton;

@Named
@Singleton
public class TransformerFactoryProvider {

    public TransformerFactory provide() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        // Issue 79 - Protect against XXE attacks
        // https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        return transformerFactory;
    }
}
