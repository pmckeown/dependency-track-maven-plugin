package io.github.pmckeown.dependencytrack.suppressions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AnalysisComment {

    private Integer timestamp;

    private String comment;

    private String commenter;

    @JsonCreator
    public AnalysisComment(
        @JsonProperty("timestamp") Integer timestamp,
        @JsonProperty("comment") String comment,
        @JsonProperty("commenter") String commenter
    ) {
        this.timestamp = timestamp;
        this.comment = comment;
        this.commenter = commenter;
    }

    @XmlElement
    public Integer getTimestamp() {
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
