package com.armedia.acm.services.notification.model;

import com.armedia.acm.data.AcmEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by armdev on 10/08/14.
 */
@Entity
@Table(name = "acm_notification")
public class Notification implements Serializable, AcmEntity

{
    private static final long serialVersionUID = -1154137631399833851L;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Id
    @Column(name = "cm_notification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "cm_notification_note")
    private String note;

    @Column(name = "cm_notification_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_notification_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_notification_user", insertable = true, updatable = true)
    private String user;

    @Column(name = "cm_notification_data", insertable = true, updatable = true)
    private String data;

    @Column(name = "cm_notification_status", insertable = true, updatable = true)
    private String status;

    @Column(name = "cm_notification_action", insertable = true, updatable = true)
    private String action;

    @Column(name = "cm_notification_comment", insertable = true, updatable = true)
    private String comment;

    @Column(name = "cm_notification_modified", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_notification_modifier", insertable = true, updatable = false)
    private String modifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}


