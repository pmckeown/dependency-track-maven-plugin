package io.github.pmckeown.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Singleton;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

/**
 * Encodes a BOM file in the Base64 format.
 *
 * @author Paul McKeown
 */
@Singleton
public class BomEncoder {

    /**
     * Encodes the file found at the provided location.
     *
     * <p>Guarantees to return an {@link Optional} containing the Base64 encoded file if one is found
     * at the provided location. An empty {@link Optional} will be returned if the file location if
     * invalid or the file cannot be read.
     *
     * @param bomLocation the location to find the file to encode
     * @param logger Common logging wrapper
     * @return an optional that will contain the Base64 encoded file or an empty optional
     */
    public Optional<String> encodeBom(String bomLocation, Logger logger) {
        logger.debug("Current working directory: %s", System.getProperty("user.dir"));
        logger.debug("looking for bom.xml at %s", bomLocation);

        try (FileInputStream fis = new FileInputStream(bomLocation)) {
            byte[] bytes = IOUtils.toByteArray(fis);

            String base64EncodedBom = new String(Base64.encodeBase64(bytes));

            return Optional.of(base64EncodedBom);
        } catch (IOException ex) {
            return Optional.empty();
        }
    }
}
