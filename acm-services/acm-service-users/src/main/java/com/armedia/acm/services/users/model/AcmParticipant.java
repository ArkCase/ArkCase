package com.armedia.acm.services.users.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_participant")
public class AcmParticipant implements Serializable
{
    private static final long serialVersionUID = 5046781644315879063L;

    @Id
    @Column(name = "cm_participant_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_object_type")
    private String objectType;

    @Column(name = "cm_object_id")
    private Long objectId;

    @Column(name = "cm_participant_type")
    private String participantType;

    @Column(name = "cm_participant_ldap_id")
    private String participantLdapId;

    @Column(name = "cm_participant_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_participant_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_participant_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_participant_modifier")
    private String modifier;

    @PrePersist
    protected void beforeInsert()
    {
        setCreated(new Date());
        setModified(new Date());
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public Long getObjectId()
    {
        return objectId;
    }

    public void setObjectId(Long objectId)
    {
        this.objectId = objectId;
    }

    public String getParticipantType()
    {
        return participantType;
    }

    public void setParticipantType(String participantType)
    {
        this.participantType = participantType;
    }

    public String getParticipantLdapId()
    {
        return participantLdapId;
    }

    public void setParticipantLdapId(String participantLdapId)
    {
        this.participantLdapId = participantLdapId;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier(String modifier)
    {
        this.modifier = modifier;
    }
}
