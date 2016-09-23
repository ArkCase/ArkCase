package com.armedia.acm.services.notification.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "acm_notification")
public class Notification implements Serializable, AcmObject, AcmEntity

{
    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @TableGenerator(name = "acm_notification_gen",
            table = "acm_notification_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_notification",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_notification_gen")
    @Column(name = "cm_notification_id")
    private Long id;


    @Column(name = "cm_notification_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_notification_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_notification_status", insertable = true, updatable = true)
    private String status;

    @Column(name = "cm_notification_action", insertable = true, updatable = true)
    private String action;

    @Column(name = "cm_notification_title", insertable = true, updatable = false)
    private String title;

    @Lob
    @Column(name = "cm_notification_note", insertable = true, updatable = true)
    private String note;

    @Column(name = "cm_modified", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_modifier", insertable = true, updatable = true)
    private String modifier;

    @Column(name = "cm_notification_user", insertable = true, updatable = true)
    private String user;

    @Column(name = "cm_notification_data", insertable = true, updatable = true)
    private String data;

    @Column(name = "cm_notification_type", insertable = true, updatable = true)
    private String type;

    @Column(name = "cm_notification_state", insertable = true, updatable = true)
    private String state;

    @Column(name = "cm_notification_parent_id")
    private Long parentId;

    @Column(name = "cm_notification_parent_type")
    private String parentType;

    @Column(name = "cm_notification_parent_name")
    private String parentName;

    @Column(name = "cm_notification_parent_title")
    private String parentTitle;

    @Column(name = "cm_related_object_id")
    private Long relatedObjectId;

    @Column(name = "cm_related_object_type")
    private String relatedObjectType;

    @Transient
    private String userEmail;

    @Column(name = "cm_notification_link")
    private String link;

    @Override
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    @Override
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    @Override
    public String getCreator()
    {
        return creator;
    }

    @Override
    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public String getType()
    {
        return type;
    }


    public void setType(String type)
    {
        this.type = type;
    }


    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    @Override
    public Date getModified()
    {
        return modified;
    }

    @Override
    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    @Override
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getParentType()
    {
        return parentType;
    }

    public void setParentType(String parentType)
    {
        this.parentType = parentType;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public String getParentTitle()
    {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle)
    {
        this.parentTitle = parentTitle;
    }

    public Long getRelatedObjectId()
    {
        return relatedObjectId;
    }

    public void setRelatedObjectId(Long relatedObjectId)
    {
        this.relatedObjectId = relatedObjectId;
    }

    public String getRelatedObjectType()
    {
        return relatedObjectType;
    }

    public void setRelatedObjectType(String relatedObjectType)
    {
        this.relatedObjectType = relatedObjectType;
    }

    public String getUserEmail()
    {
        return userEmail;
    }

    public void setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return NotificationConstants.OBJECT_TYPE;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }
}


