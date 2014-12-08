package com.armedia.acm.plugins.admin.model;

import java.nio.file.attribute.FileTime;
import java.util.Date;

/**
 * Created by manoj.dhungana on 12/4/2014.
 */
public class TemplateUpload {
    private String name;
    private String path;
    private String creator;
    private String created;
    private String modified;
    private long id;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

}
