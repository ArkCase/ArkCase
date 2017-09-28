package com.armedia.acm.objectdiff;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class AcmChange implements Serializable {
    private String action;
    private String path;
    private Long affectedObjectId;
    private String affectedObjectType;
    private String property;

    public Long getAffectedObjectId() {
        return affectedObjectId;
    }

    public void setAffectedObjectId(Long affectedObjectId) {
        this.affectedObjectId = affectedObjectId;
    }

    public String getAffectedObjectType() {
        return affectedObjectType;
    }

    public void setAffectedObjectType(String affectedObjectType) {
        this.affectedObjectType = affectedObjectType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
