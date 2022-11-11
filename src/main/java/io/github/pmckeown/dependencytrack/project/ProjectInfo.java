package io.github.pmckeown.dependencytrack.project;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ProjectInfo {

    private String publisher;
    private String description;
    private String classifier;
    private String group;
    private String purl;
    private String cpe;
    private String swidTagId;

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
        if (this.classifier != null) {
            this.classifier = this.classifier.toUpperCase();
        }
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getCpe() {
        return cpe;
    }

    public void setCpe(String cpe) {
        this.cpe = cpe;
    }

    public String getSwidTagId() {
        return swidTagId;
    }

    public void setSwidTagId(String swidTagId) {
        this.swidTagId = swidTagId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
