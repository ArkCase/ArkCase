package com.armedia.acm.services.participants.model;

import com.armedia.acm.data.AcmEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "acm_participant")
public class AcmParticipant implements Serializable, AcmEntity
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "cm_participant_id")
    private List<AcmParticipantPrivilege> privileges = new ArrayList<>();

    @PrePersist
    public void beforeInsert()
    {
        updatePrivileges();
    }

    @PreUpdate
    public void beforeUpdate()
    {
        updatePrivileges();
    }

    private void updatePrivileges()
    {
        for ( AcmParticipantPrivilege privilege : getPrivileges() )
        {
            privilege.setParticipant(this);
        }
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

    public List<AcmParticipantPrivilege> getPrivileges()
    {
        return privileges;
    }

    public void setPrivileges(List<AcmParticipantPrivilege> privileges)
    {
        this.privileges = privileges;
    }
}
