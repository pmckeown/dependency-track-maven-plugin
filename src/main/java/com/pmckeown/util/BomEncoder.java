package com.pmckeown.util;

import org.codehaus.plexus.util.Base64;
import org.codehaus.plexus.util.IOUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

public class BomEncoder {

    public Optional<String> encodeBom(String bomLocation) {
        String dir = System.getProperty("user.dir");
        try (FileInputStream fis = new FileInputStream(bomLocation)) {
            byte[] bytes = IOUtil.toByteArray(fis);

            String base64EncodedBom = new String(Base64.encodeBase64(bytes));

            return Optional.of(base64EncodedBom);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }
}
