package com.armedia.acm.data;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Update entity objects with the creator, created, modifier, and modified audit fields; thus, the application just
 * updates the entities based on business requirements, and this adapter takes care of the audit fields.
 *
 * EclipseLink JPA provider raises aboutToInsert and aboutToUpdate <strong>after</strong> it has calculated the
 * change set, and <strong>only</strong> if the objects actually have changes.  That means we have to update the
 * EclipseLink change set directly; and we also update the POJO entity fields, so the client sees the updates.
 */
public class AuditPropertyEntityAdapter extends DescriptorEventAdapter
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ThreadLocal<String> userId = new ThreadLocal<>();

    @Override
    public void aboutToInsert(DescriptorEvent event)
    {
        super.aboutToInsert(event);

        Record record = event.getRecord();
        Object data = event.getObject();

        // this method is called after JPA already computed what will be inserted.  So we have to modify the
        // insert record directly, instead of just modifying the AcmEntity.
        if ( data instanceof AcmEntity )
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("Entity type '" + data.getClass() + "' is an AcmEntity, setting insert fields.");
            }

            AcmEntity entity = (AcmEntity) data;

            Date today = new Date();
            String user = getUserId();

            String created = getDatabaseColumnName(event, AcmEntity.CREATED_PROPERTY_NAME);
            record.put(created, today);

            String creator = getDatabaseColumnName(event, AcmEntity.CREATOR_PROPERTY_NAME);
            record.put(creator, getUserId());

            // some entities, notably AcmNote, do not support mods, and so they will not have modified or modifier.
            String modified = getDatabaseColumnName(event, AcmEntity.MODIFIED_PROPERTY_NAME);
            if ( modified != null )
            {
                record.put(modified, today);
            }

            String modifier = getDatabaseColumnName(event, AcmEntity.MODIFIER_PROPERTY_NAME);
            if ( modifier != null )
            {
                record.put(modifier, getUserId());
            }

            // note, we still have to update the object itself, so the client will have the right values

            entity.setCreated(today);
            entity.setModified(today);
            entity.setCreator(user);
            entity.setModifier(user);
        }
        else
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("Entity type '" + data.getClass() + "' is NOT an AcmEntity, NOT setting insert fields.");
            }
        }
    }

    private String getDatabaseColumnName(DescriptorEvent event, String pojoPropertyName)
    {
        DatabaseMapping mapping = event.getDescriptor().getMappingForAttributeName(pojoPropertyName);
        return mapping == null ? null : mapping.getField().getName();
    }

    @Override
    public void aboutToUpdate(DescriptorEvent event)
    {
        super.aboutToUpdate(event);

        Record record = event.getRecord();
        Object data = event.getObject();

        // this method is called after JPA already computed what will be inserted.  So we have to modify the
        // insert record directly, instead of just modifying the AcmEntity.
        if ( data instanceof AcmEntity )
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("Entity type '" + data.getClass() + "' is an AcmEntity, setting update fields.");
            }

            AcmEntity entity = (AcmEntity) data;

            Date today = new Date();
            String user = getUserId();

            String modified = getDatabaseColumnName(event, AcmEntity.MODIFIED_PROPERTY_NAME);
            if ( modified != null )
            {
                record.put(modified, today);
            }

            String modifier = getDatabaseColumnName(event, AcmEntity.MODIFIER_PROPERTY_NAME);
            if ( modifier != null )
            {
                record.put(modifier, getUserId());
            }

            // note, we still have to update the object itself, so the client will have the right values

            entity.setModified(today);
            entity.setModifier(user);
        }
        else
        {
            if ( log.isTraceEnabled() )
            {
                log.trace("Entity type '" + data.getClass() + "' is NOT an AcmEntity, NOT setting update fields.");
            }
        }
    }

    public String getUserId()
    {
        return userId.get();
    }

    public void setUserId(String userId)
    {
        this.userId.set(userId);
    }
}
