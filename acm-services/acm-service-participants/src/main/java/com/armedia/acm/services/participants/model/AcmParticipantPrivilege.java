package com.armedia.acm.services.participants.model;

import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acm_participant_privilege")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmParticipantPrivilege implements Serializable, AcmEntity
{
    private static final long serialVersionUID = -2774839599422346798L;

    @Id
    @TableGenerator(name = "acm_participant_privilege_gen",
            table = "acm_participant_privilege_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_participant_privilege",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_participant_privilege_gen")
    @Column(name = "cm_privilege_id")
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cm_participant_id", nullable = false)
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
