package com.armedia.acm.services.subscription.model;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class AcmSubscription implements AcmObject, AcmEntity {

    @Id
    @Column(name = "cm_subscription_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    @Column(name = "cm_user_id", nullable = false, insertable = true, updatable = false)
    private String userId;

    @Column(name = "cm_object_id", nullable = false, insertable = true, updatable = false)
    private Long objectId;

    @Column(name = "cm_object_type", nullable = false, insertable = true, updatable = false)
    private String objectType;

    @Column(name = "cm_object_number", nullable = false)
    private String objectNumber;

    @Column(name = "cm_object_name", nullable = false)
    private String objectName;

    @Column(name = "cm_subscription_creator", nullable = false, insertable = true, updatable = false)
    private String creator;

    @Column(name = "cm_subscription_modifier", nullable = false, insertable = true, updatable = true)
    private String modifier;

    @Column(name = "cm_subscription_created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "cm_subscription_modified", nullable = false, insertable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectNumber() {
        return objectNumber;
    }

    public void setObjectNumber(String objectNumber) {
        this.objectNumber = objectNumber;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier;
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
    public Date getModified() {
        return modified;
    }

    @Override
    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    @JsonIgnore
    public String getObjectType() {
        return "SUBSCRIPTION";
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return subscriptionId;
    }
}
