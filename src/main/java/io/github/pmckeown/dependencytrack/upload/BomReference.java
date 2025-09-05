package io.github.pmckeown.dependencytrack.upload;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BomReference {
    private final File file;
    private final String string;

    public BomReference(File bomFile) {
        if (bomFile == null) {
            throw new NullPointerException("bom file cannot be null");
        }
        this.file = bomFile;
        this.string = null;
    }

    public BomReference(String bom) {
        if (bom == null) {
            throw new NullPointerException("bom cannot be null");
        }
        this.string = bom;
        this.file = null;
    }

    public boolean isFileReference() {
        return file != null;
    }

    public File getFile() {
        return file;
    }

    /**
     * Create new input stream for the BOM.
     *
     * @return InputStream A new input stream to the BOM.
     * @throws IOException Throw when the input stream could not be created.
     */
    public InputStream getInputStream() throws IOException {
        if (isFileReference()) {
            return new FileInputStream(file);
        } else {
            return ReaderInputStream.builder()
                    // The SBOM is either XML or JSON, so it should be read as UTF-8
                    .setCharset(StandardCharsets.UTF_8)
                    .setReader(new StringReader(string))
                    .get();
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    /**
     * Jackson serializer which serializes the BOM as a base64 encoded value.
     */
    public static class Base64Serializer extends JsonSerializer<BomReference> {
        @Override
        public void serialize(BomReference value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            try (InputStream is = value.getInputStream()) {
                gen.writeBinary(is, -1);
            }
        }
    }
}
