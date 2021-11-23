package com.armedia.acm.data;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Record;

import java.util.Date;

/**
 * Update entity objects with the creator, created, modifier, and modified audit fields; thus, the application just
 * updates the entities based on business requirements, and this adapter takes care of the audit fields.
 * <p>
 * EclipseLink JPA provider raises aboutToInsert and aboutToUpdate <strong>after</strong> it has calculated the
 * change set, and <strong>only</strong> if the objects actually have changes. That means we have to update the
 * EclipseLink change set directly; and we also update the POJO entity fields, so the client sees the updates.
 */
public class AuditPropertyEntityAdapter extends DescriptorEventAdapter
{
    private final Logger log = LogManager.getLogger(getClass());
    private ThreadLocal<String> userId = new ThreadLocal<>();

    @Override
    public void aboutToInsert(DescriptorEvent event)
    {
        super.aboutToInsert(event);

        Record record = event.getRecord();
        Object data = event.getObject();

        // this method is called after JPA already computed what will be inserted. So we have to modify the
        // insert record directly, instead of just modifying the AcmEntity.
        if (data instanceof AcmEntity)
        {
            log.trace("Entity type [{}] is an AcmEntity, setting insert fields.", data.getClass());

            AcmEntity entity = (AcmEntity) data;

            Date today = new Date();
            String user = getUserId();

            String created = getDatabaseColumnName(event, AcmEntity.CREATED_PROPERTY_NAME);
            if (entity.getCreated() == null)
            {
                record.put(created, today);
                entity.setCreated(today);
            }

            String creator = getDatabaseColumnName(event, AcmEntity.CREATOR_PROPERTY_NAME);
            if (entity.getCreator() == null)
            {
                record.put(creator, getUserId());
                entity.setCreator(user);
            }

            // some entities, notably AcmNote, do not support mods, and so they will not have modified or modifier.
            String modified = getDatabaseColumnName(event, AcmEntity.MODIFIED_PROPERTY_NAME);
            if (modified != null && entity.getModified() == null)
            {
                record.put(modified, today);
                entity.setModified(today);
            }

            String modifier = getDatabaseColumnName(event, AcmEntity.MODIFIER_PROPERTY_NAME);
            if (modifier != null && entity.getModifier() == null)
            {
                record.put(modifier, getUserId());
                entity.setModifier(user);
            }

            // note, we still have to update the object itself, so the client will have the right values
        }
        else
        {
            log.trace("Entity type [{}] is NOT an AcmEntity, NOT setting insert fields.", data.getClass());
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

        // this method is called after JPA already computed what will be inserted. So we have to modify the
        // insert record directly, instead of just modifying the AcmEntity.
        if (data instanceof AcmEntity)
        {
            log.trace("Entity type [{}] is an AcmEntity, setting update fields.", data.getClass());

            AcmEntity entity = (AcmEntity) data;

            Date today = new Date();
            String user = getUserId();

            String modified = getDatabaseColumnName(event, AcmEntity.MODIFIED_PROPERTY_NAME);
            if (modified != null)
            {
                record.put(modified, today);
            }

            String modifier = getDatabaseColumnName(event, AcmEntity.MODIFIER_PROPERTY_NAME);
            if (modifier != null)
            {
                /*
                We are doing this to not change the modifier on manually triggered updates (which are done to update SOLR data) ACFP-1336.
                All other changes outside of just changing the modified date manually will result with a change in the modifier.
                */
                boolean hasMeaningfulChanges = record.size() > 1 || record.get(modified) == null;

                if (hasMeaningfulChanges)
                {
                    record.put(modifier, getUserId());
                    entity.setModifier(user);
                }

            }

            // note, we still have to update the object itself, so the client will have the right values
            entity.setModified(today);
        }
        else
        {
            log.trace("Entity type [{}] is NOT an AcmEntity, NOT setting update fields.", data.getClass());
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
