package io.github.pmckeown.dependencytrack.suppressions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * POJO for an analysis comment which is part of the response from the Dependency-Track API.
 *
 * @author Thomas Hucke
 */
public class AnalysisComment {

    private final Long timestamp;

    private final String comment;

    private final String commenter;

    @JsonCreator
    public AnalysisComment(
        @JsonProperty("timestamp") Long timestamp,
        @JsonProperty("comment") String comment,
        @JsonProperty("commenter") String commenter
    ) {
        this.timestamp = timestamp;
        this.comment = comment;
        this.commenter = commenter;
    }

    @XmlElement
    public Long getTimestamp() {
        return timestamp;
    }

    @XmlElement
    public String getComment() {
        return comment;
    }

    @XmlElement
    public String getCommenter() {
        return commenter;
    }

    @Override
    public String toString() { return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE); }
}
