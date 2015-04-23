package com.armedia.acm.service.outlook.model;

import java.util.Date;

/**
 * Created by armdev on 4/21/15.
 */
public abstract class OutlookItem
{
    private String id;
    private String subject;
    private Date created;
    private Date modified;
    private String body;
    private int size;
    private Date sent;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getSize()
    {
        return size;
    }

    public void setSent(Date sent)
    {
        this.sent = sent;
    }

    public Date getSent()
    {
        return sent;
    }

    @Override
    public String toString()
    {
        return "OutlookItem{" +
                "id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                ", body='" + body + '\'' +
                ", size=" + size +
                ", sent=" + sent +
                '}';
    }


}
