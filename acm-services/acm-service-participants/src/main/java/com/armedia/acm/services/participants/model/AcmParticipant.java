package com.armedia.acm.services.participants.model;

import com.armedia.acm.core.AcmNotificationReceiver;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "acm_participant")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "className", defaultImpl = AcmParticipant.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cm_class_name", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("com.armedia.acm.services.participants.model.AcmParticipant")
public class AcmParticipant implements Serializable, AcmEntity, AcmNotificationReceiver
{
    private static final long serialVersionUID = 5046781644315879063L;

    @Id
    @TableGenerator(name = "acm_participant_gen",
            table = "acm_participant_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_participant",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_participant_gen")
    @Column(name = "cm_participant_id")
    private Long id;

    @Column(name = "cm_class_name")
    private String className = this.getClass().getName();

    @Column(name = "cm_object_type", insertable = true, updatable = false)
    private String objectType;

    @Column(name = "cm_object_id", insertable = true, updatable = false)
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participant", fetch = FetchType.EAGER, orphanRemoval = true)
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
        for (AcmParticipantPrivilege privilege : getPrivileges())
        {
            if (privilege.getParticipant() == null)
            {
                privilege.setParticipant(this);
            }
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

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
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

    @Override
    public String toString()
    {
        return "AcmParticipant{" +
                "id=" + id +
                ", objectType='" + objectType + '\'' +
                ", objectId=" + objectId +
                ", participantType='" + participantType + '\'' +
                ", participantLdapId='" + participantLdapId + '\'' +
                ", created=" + created +
                ", creator='" + creator + '\'' +
                ", modified=" + modified +
                ", modifier='" + modifier + '\'' +
                ", privileges=" + privileges +
                '}';
    }

    @Override
    public boolean equals(Object obj)
    {
        Objects.requireNonNull(obj, "Comparable object must not be null");
        if (!(obj instanceof AcmParticipant))
            return false;
        AcmParticipant other = (AcmParticipant) obj;
        if (this.getId() == null || other.getId() == null)
            return false;
        return getId().equals(other.getId());
    }

    @Override
    public int hashCode()
    {
        if (getId() == null)
            return super.hashCode();
        else
            return getId().hashCode();
    }

    @Override
    @JsonIgnore
    public String getReceiverLdapId()
    {
        return participantLdapId;
    }

    @Override
    @JsonIgnore
    public String getReceiverType()
    {
        return participantType;
    }
}
