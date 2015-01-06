package com.armedia.acm.services.participants.model;

import com.armedia.acm.data.AcmEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_participant_privilege")
public class AcmParticipantPrivilege implements Serializable, AcmEntity
{
    private static final long serialVersionUID = -2774839599422346798L;

    @Id
    @Column(name = "cm_privilege_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cm_privilege_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_privilege_creator", insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_privilege_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_privilege_modifier")
    private String modifier;

    @Column(name = "cm_object_action")
    private String objectAction;

    @Column(name = "cm_access_type")
    private String accessType;

    @Column(name = "cm_access_reason")
    private String accessReason;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_participant_id")
    private AcmParticipant participant;

    public String getObjectAction()
    {
        return objectAction;
    }

    public void setObjectAction(String objectAction)
    {
        this.objectAction = objectAction;
    }

    public String getAccessType()
    {
        return accessType;
    }

    public void setAccessType(String accessType)
    {
        this.accessType = accessType;
    }

    public String getAccessReason()
    {
        return accessReason;
    }

    public void setAccessReason(String accessReason)
    {
        this.accessReason = accessReason;
    }

    public AcmParticipant getParticipant()
    {
        return participant;
    }

    public void setParticipant(AcmParticipant participant)
    {
        this.participant = participant;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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
}
