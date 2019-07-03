package com.pmckeown.util;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.Base64;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;

public class BomUtils {

    private BomUtils() {
        // Utility class
    }

    /**
     * Get a Base64 encoded string containing the contents of the file located at the given filename.
     * A maven {@link Log} is required to ensure logs are written using the expected Maven formatting.
     *
     * @param bomFilePath the location of the BOM file
     * @param log the Maven Logger
     * @return a Base64 encoded string of the BOM file contents
     */
    public static Optional<String> getBase64EncodedBom(String bomFilePath, Log log) {
        AtomicReference<Optional<String>> base64EncodedBomOptional = new AtomicReference<>();

        if (FileUtils.fileExists(bomFilePath)) {
            try (FileInputStream fis = new FileInputStream(bomFilePath)) {
                byte[] bytes = IOUtil.toByteArray(fis);
                if (log.isDebugEnabled()) {
                    log.debug(new String(bytes));
                }

                String base64EncodedBom = new String(Base64.encodeBase64(bytes));
                if (log.isDebugEnabled()) {
                    log.debug(base64EncodedBom);
                }

                base64EncodedBomOptional.set(Optional.of(base64EncodedBom));
            } catch (IOException ex) {
                log.error("Error during loading and encoding bom file", ex);
                base64EncodedBomOptional.set(Optional.empty());
            }

        } else {
            log.debug(format("Filename not found: %s", bomFilePath));
            base64EncodedBomOptional.set(Optional.empty());
        }
        return base64EncodedBomOptional.get();
    }
}
