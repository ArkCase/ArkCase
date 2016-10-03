package com.armedia.acm.data;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.internal.sessions.DirectToFieldChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.sessions.changesets.ChangeRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ObjectChangesBySessionAccumulator extends DescriptorEventAdapter
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private Map<String, AcmObjectChangelist> changesBySession =
            Collections.synchronizedMap(new HashMap<String, AcmObjectChangelist>());

    private Map<String, List<AcmEntityChangesHolder>> entityChangesBySession =
            Collections.synchronizedMap(new HashMap<>());

    @Override
    public void postInsert(DescriptorEvent event)
    {
        super.postInsert(event);
        log.trace("After insert: {}", event.getObject().getClass().getName());

        try
        {
            String sessionName = event.getSession().getName();
            if (event.getQuery() instanceof InsertObjectQuery)
            {
                InsertObjectQuery insertObjectQuery = (InsertObjectQuery) event.getQuery();
                ObjectChangeSet objectChanges = insertObjectQuery.getObjectChangeSet();
                AcmEntityChangesHolder acmEntityChangesHolder = processChangeRecords(objectChanges,
                        AcmEntityChangeEvent.ACTION.INSERT);
                getEntityChangesBySession().get(sessionName).add(acmEntityChangesHolder);
            }
            getChangesBySession().get(sessionName).getAddedObjects().add(event.getObject());
        } catch (NullPointerException npe)
        {
            String nullPart = "[???]";
            if (getChangesBySession() == null)
            {
                nullPart = "getChangesBySession";
            } else if (event == null)
            {
                nullPart = "event";
            } else if (event.getSession() == null)
            {
                nullPart = "event.getSession";
            } else if (event.getSession().getName() == null)
            {
                nullPart = "event.getSession().getName()";
            } else if (getChangesBySession().get(event.getSession().getName()) == null)
            {
                nullPart = "getChangesBySession().get(event.getSession().getName())";
            } else if (event.getObject() == null)
            {
                nullPart = "event.getObject()";
            } else if (getChangesBySession().get(event.getSession().getName()).getAddedObjects() == null)
            {
                nullPart = "getChangesBySession().get(event.getSession().getName()).getAddedObjects()";
            }
            log.error("Mystery Null Pointer Exception: Null part is... " + nullPart);
        }
    }

    @Override
    public void postUpdate(DescriptorEvent event)
    {
        super.postUpdate(event);
        log.trace("After update: {}", event.getObject().getClass().getName());

        try
        {
            String sessionName = event.getSession().getName();
            if (event.getQuery() instanceof UpdateObjectQuery)
            {
                UpdateObjectQuery updateObjectQuery = (UpdateObjectQuery) event.getQuery();
                ObjectChangeSet objectChanges = updateObjectQuery.getObjectChangeSet();
                AcmEntityChangesHolder acmEntityChangesHolder = processChangeRecords(objectChanges,
                        AcmEntityChangeEvent.ACTION.UPDATE);
                getEntityChangesBySession().get(sessionName).add(acmEntityChangesHolder);
            }
            getChangesBySession().get(sessionName).getUpdatedObjects().add(event.getObject());
        } catch (NullPointerException npe)
        {
            String nullPart = "[???]";
            if (getChangesBySession() == null)
            {
                nullPart = "getChangesBySession";
            } else if (event == null)
            {
                nullPart = "event";
            } else if (event.getSession() == null)
            {
                nullPart = "event.getSession";
            } else if (event.getSession().getName() == null)
            {
                nullPart = "event.getSession().getName()";
            } else if (getChangesBySession().get(event.getSession().getName()) == null)
            {
                nullPart = "getChangesBySession().get(event.getSession().getName())";
            } else if (event.getObject() == null)
            {
                nullPart = "event.getObject()";
            } else if (getChangesBySession().get(event.getSession().getName()).getUpdatedObjects() == null)
            {
                nullPart = "getChangesBySession().get(event.getSession().getName()).getUpdatedObjects()";
            }
            log.error("Mystery Null Pointer Exception: Null part is... " + nullPart);
        }


    }

    private AcmEntityChangesHolder processChangeRecords(ObjectChangeSet objectChanges, AcmEntityChangeEvent.ACTION changeAction)
    {
        List<ChangeRecord> changeRecords = objectChanges.getChanges();
        log.debug("Process changes -> class:{}", objectChanges.getClassName());
        AcmEntityChangesHolder acmEntityChangesHolder = new AcmEntityChangesHolder(objectChanges.getClassName(),
                objectChanges.getId().toString(), changeAction);
        changeRecords.stream()
                .filter(changeRecord -> changeRecord instanceof DirectToFieldChangeRecord)
                .forEach(changeRecord ->
                {
                    DirectToFieldChangeRecord directToFieldChangeRecord = (DirectToFieldChangeRecord) changeRecord;
                    log.debug("Change: {} : -> {} -> {}", directToFieldChangeRecord.getAttribute(),
                            directToFieldChangeRecord.getOldValue(),
                            directToFieldChangeRecord.getNewValue());
                    acmEntityChangesHolder.addPropertyChange(directToFieldChangeRecord.getAttribute(),
                            directToFieldChangeRecord.getOldValue(), directToFieldChangeRecord.getNewValue());
                });
        return acmEntityChangesHolder;
    }

    private AcmEntityChangesHolder processDeleteQuery(DeleteObjectQuery deleteObjectQuery){
        String entityId = deleteObjectQuery.getPrimaryKey().toString();
        String entityClass = deleteObjectQuery.getObject().getClass().getName();
        return new AcmEntityChangesHolder(entityClass, entityId, AcmEntityChangeEvent.ACTION.DELETE);
    }

    @Override
    public void postDelete(DescriptorEvent event)
    {
        super.postDelete(event);
        log.trace("After delete: {}", event.getObject().getClass().getName());

        try
        {
            String sessionName = event.getSession().getName();
            if (event.getQuery() instanceof DeleteObjectQuery)
            {
                DeleteObjectQuery deleteObjectQuery = (DeleteObjectQuery) event.getQuery();
                AcmEntityChangesHolder acmEntityChangesHolder = processDeleteQuery(deleteObjectQuery);
                getEntityChangesBySession().get(sessionName).add(acmEntityChangesHolder);
            }
            getChangesBySession().get(sessionName).getDeletedObjects().add(event.getObject());
        } catch (NullPointerException npe)
        {
            String nullPart = "[???]";
            if (getChangesBySession() == null)
            {
                nullPart = "getChangesBySession";
            } else if (event == null)
            {
                nullPart = "event";
            } else if (event.getSession() == null)
            {
                nullPart = "event.getSession";
            } else if (event.getSession().getName() == null)
            {
                nullPart = "event.getSession().getName()";
            } else if (getChangesBySession().get(event.getSession().getName()) == null)
            {
                nullPart = "getChangesBySession().get(event.getSession().getName())";
            } else if (event.getObject() == null)
            {
                nullPart = "event.getObject()";
            } else if (getChangesBySession().get(event.getSession().getName()).getDeletedObjects() == null)
            {
                nullPart = "getChangesBySession().get(event.getSession().getName()).getDeletedObjects()";
            }
            log.error("Mystery Null Pointer Exception: Null part is... " + nullPart);
        }


    }

    public Map<String, AcmObjectChangelist> getChangesBySession()
    {
        return changesBySession;
    }

    public void setChangesBySession(Map<String, AcmObjectChangelist> changesBySession)
    {
        this.changesBySession = changesBySession;
    }

    public Map<String, List<AcmEntityChangesHolder>> getEntityChangesBySession()
    {
        return entityChangesBySession;
    }

    public void setEntityChangesBySession(Map<String, List<AcmEntityChangesHolder>> entityChangesBySession)
    {
        this.entityChangesBySession = entityChangesBySession;
    }
}
