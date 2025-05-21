package io.github.pmckeown.dependencytrack.bom;

import io.github.pmckeown.dependencytrack.project.ProjectInfo;
import io.github.pmckeown.util.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Component;
import org.cyclonedx.parsers.BomParserFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.Optional;

/**
 * Encodes a BOM file in the Base64 format.
 *
 * @author Paul McKeown
 */
@Singleton
public class BomParser {

    private Logger logger;

    @Inject
    public BomParser(Logger logger) {
        this.logger = logger;
    }

    /**
     * Parses the Project Info from a BOM {@link File}.
     *
     * Guarantees to return an {@link Optional} containing a {@link ProjectInfo} object if the provided BOM file can
     * be parsed successfully.
     * An empty {@link Optional} will be returned if the file cannot be parsed.
     *
     * @param bomFile File containing a BOM
     * @return an optional that will contain the parsed {@link ProjectInfo} or an empty optional
     */
    public Optional<ProjectInfo> getProjectInfo(File bomFile) {
        if (!bomFile.canRead()) {
            logger.warn("Can not read bom {}", bomFile);
            return Optional.empty();
        }
        Bom bom;
        try (BOMInputStream bis = BOMInputStream.builder().setFile(bomFile).setInclude(false).get()) {
            byte[] bytes = IOUtils.toByteArray(bis);
            bom = BomParserFactory.createParser(bytes).parse(bytes);
        } catch (Exception ex) {
            logger.warn("Failed to update project info. Failure processing bom.", ex);
            return Optional.empty();
        }

        if (bom.getMetadata() == null || bom.getMetadata().getComponent() == null) {
            return Optional.empty();
        }

        Component component =  bom.getMetadata().getComponent();
        ProjectInfo info = new ProjectInfo();
        if (component.getType() != null) {
            info.setClassifier(component.getType().name());
        }
        info.setAuthor(component.getAuthor());
        info.setPublisher(component.getPublisher());
        info.setDescription(component.getDescription());
        info.setGroup(component.getGroup());
        info.setPurl(component.getPurl());
        info.setCpe(component.getCpe());
        if (component.getSwid() != null) {
            info.setSwidTagId(component.getSwid().getTagId());
        }
        return Optional.of(info);
    }
}
