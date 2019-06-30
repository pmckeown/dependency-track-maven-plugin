package com.pmckeown.util;

import org.codehaus.plexus.util.Base64;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class BomUtils {

    private BomUtils() {
        // Utility class
    }

    public static Optional<String> getBase64EncodedBom(String filename) {
        AtomicReference<Optional<String>> base64EncodedBomOptional = new AtomicReference<>();

        if (FileUtils.fileExists(filename)) {
            try (FileInputStream fis = new FileInputStream(filename)) {
                byte[] bytes = IOUtil.toByteArray(fis);
                base64EncodedBomOptional.set(Optional.of(new String(Base64.encodeBase64(bytes))));
            } catch (IOException ex) {
                base64EncodedBomOptional.set(Optional.empty());
            }

        } else {
            base64EncodedBomOptional.set(Optional.empty());
        }
        return base64EncodedBomOptional.get();
    }
}
