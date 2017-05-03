package com.armedia.acm.services.subscription.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmParentObjectInfo;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
@Entity
@Table(name = "acm_subscription_event")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class AcmSubscriptionEvent implements AcmObject, AcmEntity, AcmParentObjectInfo
{

    @Id
    @TableGenerator(name = "acm_subscription_event_gen",
            table = "acm_subscription_event_id",
            pkColumnName = "cm_seq_name",
            valueColumnName = "cm_seq_num",
            pkColumnValue = "acm_subscription_event",
            initialValue = 100,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acm_subscription_event_gen")
    @Column(name = "cm_event_id")
    private Long subscriptionEventId;

    @Column(name = "cm_event_object_type", insertable = true, updatable = false)
    private String eventObjectType;

    @Column(name = "cm_event_object_id", insertable = true, updatable = false)
    private Long eventObjectId;

    @Column(name = "cm_event_user")
    private String eventUser;

    @Column(name = "cm_event_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;

    @Column(name = "cm_event_subscription_owner", nullable = false)
    private String subscriptionOwner;

    @Column(name = "cm_event_type", nullable = false)
    private String eventType;

    @Column(name = "cm_event_object_name", nullable = false)
    private String eventObjectName;

    @Column(name = "cm_event_object_number", nullable = false)
    private String eventObjectNumber;

    @Column(name = "cm_event_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_event_modifier", nullable = false, insertable = true, updatable = true)
    private String modifier;

    @Column(name = "cm_event_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_event_modified", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "cm_subscription_id")
    private String relatedSubscriptionId;

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
    public String getModifier()
    {
        return modifier;
    }

    @Override
    public void setModifier(String modifier)
    {
        this.modifier = modifier;
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
    public Date getCreated()
    {
        return created;
    }

    @Override
    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Long getSubscriptionEventId()
    {
        return subscriptionEventId;
    }

    public void setSubscriptionEventId(Long subscriptionEventId)
    {
        this.subscriptionEventId = subscriptionEventId;
    }

    public String getEventObjectType()
    {
        return eventObjectType;
    }

    public void setEventObjectType(String eventObjectType)
    {
        this.eventObjectType = eventObjectType;
    }

    public Long getEventObjectId()
    {
        return eventObjectId;
    }

    public void setEventObjectId(Long eventObjectId)
    {
        this.eventObjectId = eventObjectId;
    }

    public String getEventUser()
    {
        return eventUser;
    }

    public void setEventUser(String eventUser)
    {
        this.eventUser = eventUser;
    }

    public Date getEventDate()
    {
        return eventDate;
    }

    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }

    public String getSubscriptionOwner()
    {
        return subscriptionOwner;
    }

    public void setSubscriptionOwner(String subscriptionOwner)
    {
        this.subscriptionOwner = subscriptionOwner;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getEventObjectName()
    {
        return eventObjectName;
    }

    public void setEventObjectName(String eventObjectName)
    {
        this.eventObjectName = eventObjectName;
    }

    public String getEventObjectNumber()
    {
        return eventObjectNumber;
    }

    public void setEventObjectNumber(String eventObjectNumber)
    {
        this.eventObjectNumber = eventObjectNumber;
    }

    public String getRelatedSubscriptionId()
    {
        return relatedSubscriptionId;
    }

    public void setRelatedSubscriptionId(String relatedSubscriptionId)
    {
        this.relatedSubscriptionId = relatedSubscriptionId;
    }

    @Override
    @JsonIgnore
    public String getObjectType()
    {
        return SubscriptionConstants.OBJECT_TYPE_EVENT;
    }

    @Override
    @JsonIgnore
    public Long getId()
    {
        return subscriptionEventId;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Long getParentObjectId()
    {
        return eventObjectId;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getParentObjectType()
    {
        return eventObjectType;
    }
}
