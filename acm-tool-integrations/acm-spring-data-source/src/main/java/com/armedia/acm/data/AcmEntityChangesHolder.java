package com.armedia.acm.data;


import com.google.common.base.MoreObjects;

import java.util.Map;
import java.util.TreeMap;

public class AcmEntityChangesHolder
{
    private final Map<String, AcmEntityPropertyChangeHolder> entityChangeRecordMap;
    private String entityClass;
    private String entityId;
    private AcmEntityChangeEvent.ACTION entityChangeAction;

    public AcmEntityChangesHolder(String entityClass, String entityId, AcmEntityChangeEvent.ACTION entityChangeAction)
    {
        this.entityClass = entityClass;
        this.entityId = entityId;
        this.entityChangeAction = entityChangeAction;
        this.entityChangeRecordMap = new TreeMap<>();
    }

    public void addPropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        entityChangeRecordMap.put(propertyName, new AcmEntityPropertyChangeHolder(oldValue, newValue));
    }

    public Map<String, AcmEntityPropertyChangeHolder> getEntityChangeRecordMap()
    {
        return entityChangeRecordMap;
    }

    public String getEntityClass()
    {
        return entityClass;
    }

    public String getEntityId()
    {
        return entityId;
    }

    public AcmEntityChangeEvent.ACTION getEntityChangeAction()
    {
        return entityChangeAction;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("entityClass", entityClass)
                .add("entityId", entityId)
                .toString();
    }
}
