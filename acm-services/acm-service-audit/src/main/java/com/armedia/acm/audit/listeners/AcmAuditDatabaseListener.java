package com.armedia.acm.audit.listeners;

import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.model.NotAudited;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.data.AcmDatabaseChangesEvent;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.AcmObjectChangelist;
import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.web.api.AsyncApplicationListener;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listens to database changes and adds details of what has changed in the audit log. Runs asynchronously.
 * <p>
 * Created by Bojan Milenkoski on 13.1.2016.
 */
@AsyncApplicationListener
public class AcmAuditDatabaseListener implements ApplicationListener<AcmDatabaseChangesEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AuditService auditService;
    private boolean databaseChangesLoggingEnabled;
    private boolean databaseChangesLoggingFieldValuesEnabled;
    private ObjectConverter objectConverter;

    private static final String EVENT_TYPE = "com.armedia.acm.audit.database";

    /**
     * Handle an {@link AcmDatabaseChangesEvent} event.
     *
     * @param event
     *            the event to respond to
     */
    @Override
    public void onApplicationEvent(AcmDatabaseChangesEvent event)
    {
        log.debug("Database changes auditing event handling");

        if (isDatabaseChangesLoggingEnabled())
        {
            AcmObjectChangelist changelist = event.getObjectChangelist();

            // added objects
            for (Object addedObject : changelist.getAddedObjects())
            {
                if (isAuditable(addedObject))
                {
                    AuditEvent auditEvent = new AuditEvent();

                    setAuditEventProperties(auditEvent, addedObject);
                    auditEvent.setStatus(AuditConstants.EVENT_STATUS_DB_OBJECT_ADDED);
                    setAuditEventFieldsForCreatedObject(auditEvent, addedObject);

                    audit(auditEvent);
                }
            }

            // deleted objects
            for (Object deletedObject : changelist.getDeletedObjects())
            {
                if (isAuditable(deletedObject))
                {
                    AuditEvent auditEvent = new AuditEvent();

                    setAuditEventProperties(auditEvent, deletedObject);
                    auditEvent.setStatus(AuditConstants.EVENT_STATUS_DB_OBJECT_DELETED);
                    setAuditEventFieldsForModifiedObject(auditEvent, deletedObject);

                    audit(auditEvent);
                }
            }

            // updated objects
            for (Object updatedObject : changelist.getUpdatedObjects())
            {
                if (isAuditable(updatedObject))
                {
                    AuditEvent auditEvent = new AuditEvent();

                    setAuditEventProperties(auditEvent, updatedObject);
                    auditEvent.setStatus(AuditConstants.EVENT_STATUS_DB_OBJECT_UPDATED);
                    setAuditEventFieldsForModifiedObject(auditEvent, updatedObject);

                    audit(auditEvent);
                }
            }
        }
    }

    private void setAuditEventFieldsForCreatedObject(AuditEvent auditEvent, Object object)
    {
        if (object instanceof AcmEntity)
        {
            AcmEntity acmEntity = (AcmEntity) object;
            auditEvent.setEventDate(acmEntity.getCreated());
            auditEvent.setUserId(acmEntity.getCreator());
        }
        else
        {
            auditEvent.setEventDate(new Date());
            auditEvent.setUserId(getUserIdFromObject(object));
        }
    }

    private void setAuditEventFieldsForModifiedObject(AuditEvent auditEvent, Object object)
    {
        if (object instanceof AcmEntity)
        {
            AcmEntity acmEntity = (AcmEntity) object;
            auditEvent.setEventDate(acmEntity.getModified());
            auditEvent.setUserId(acmEntity.getModifier());
        }
        else
        {
            auditEvent.setEventDate(new Date());
            auditEvent.setUserId(getUserIdFromObject(object));
        }
    }

    private boolean isAuditable(Object object)
    {
        return object.getClass().getAnnotation(NotAudited.class) == null;
    }

    private void setAuditEventProperties(AuditEvent auditEvent, Object object)
    {
        auditEvent.setIpAddress(MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
        // when database is changed without web request the MDC.get(AuditConstants.EVENT_MDC_REQUEST_ID_KEY) is null
        auditEvent.setRequestId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY) == null ? null
                : UUID.fromString(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY)));
        auditEvent.setFullEventType(EVENT_TYPE + " | " + object.getClass().getName());
        auditEvent.setEventResult(AuditConstants.EVENT_RESULT_SUCCESS);
        auditEvent.setObjectType(AuditConstants.EVENT_OBJECT_TYPE_DATABASE);

        Long id = -1L;
        try
        {
            id = (Long) object.getClass().getMethod("getId").invoke(object);
        }
        catch (Exception e)
        {
            // object doesn't have Long getId() method, we'll use the default -1L
            log.debug("Object of class: " + object.getClass().getName() + " doesn't have getId() method!");
        }

        auditEvent.setObjectId(id);

        if (isDatabaseChangesLoggingFieldValuesEnabled())
        {
            // event properties
            Map<String, String> eventProperties = new HashMap<>();

            // Convert Object to JSON string
            AcmMarshaller converter = getObjectConverter().getJsonMarshaller();
            eventProperties.put("Object", converter.marshal(object));

            auditEvent.setEventProperties(eventProperties);
        }
    }

    private String getUserIdFromObject(Object object)
    {
        String userId;
        try
        {
            userId = (String) object.getClass().getMethod("getUserId").invoke(object);
        }
        catch (Exception e)
        {
            // object doesn't have String getUserId(), we'll use the one in MDC or "anonymous"
            userId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY) != null ? MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY)
                    : AuditConstants.USER_ID_ANONYMOUS;
        }
        return userId;
    }

    private void audit(AuditEvent auditEvent)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Database AuditEvent: " + auditEvent.toString());
        }

        getAuditService().audit(auditEvent);
    }

    public AuditService getAuditService()
    {
        return auditService;
    }

    public void setAuditService(AuditService auditService)
    {
        this.auditService = auditService;
    }

    public boolean isDatabaseChangesLoggingEnabled()
    {
        return databaseChangesLoggingEnabled;
    }

    public void setDatabaseChangesLoggingEnabled(boolean databaseChangesLoggingEnabled)
    {
        this.databaseChangesLoggingEnabled = databaseChangesLoggingEnabled;
    }

    public boolean isDatabaseChangesLoggingFieldValuesEnabled()
    {
        return databaseChangesLoggingFieldValuesEnabled;
    }

    public void setDatabaseChangesLoggingFieldValuesEnabled(boolean databaseChangesLoggingFieldValuesEnabled)
    {
        this.databaseChangesLoggingFieldValuesEnabled = databaseChangesLoggingFieldValuesEnabled;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
