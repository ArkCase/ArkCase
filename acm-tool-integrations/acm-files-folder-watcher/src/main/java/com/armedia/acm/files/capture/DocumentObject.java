package com.armedia.acm.files.capture;

import org.eclipse.persistence.dynamic.DynamicEntity;

import java.io.File;
import java.util.List;

/**
 * Created by riste.tutureski on 9/3/2015.
 */
public class DocumentObject
{
    private String id;
    private File document;
    private List<DocumentObject> attachments;
    private DynamicEntity entity;

    public DocumentObject()
    {
    }

    public DocumentObject(String id, File document, List<DocumentObject> attachments, DynamicEntity entity)
    {
        this.id = id;
        this.document = document;
        this.attachments = attachments;
        this.entity = entity;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public File getDocument()
    {
        return document;
    }

    public void setDocument(File document)
    {
        this.document = document;
    }

    public List<DocumentObject> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(List<DocumentObject> attachments)
    {
        this.attachments = attachments;
    }

    public DynamicEntity getEntity()
    {
        return entity;
    }

    public void setEntity(DynamicEntity entity)
    {
        this.entity = entity;
    }
}
