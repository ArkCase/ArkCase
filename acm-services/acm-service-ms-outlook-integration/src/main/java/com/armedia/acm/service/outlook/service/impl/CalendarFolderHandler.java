package com.armedia.acm.service.outlook.service.impl;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

import static com.armedia.acm.service.outlook.service.impl.AcmEntityAdapter.getContainer;
import static com.armedia.acm.service.outlook.service.impl.AcmEntityAdapter.getNotifiableEntityTitle;
import static com.armedia.acm.service.outlook.service.impl.AcmEntityAdapter.getParticipants;
import static com.armedia.acm.service.outlook.service.impl.AcmEntityAdapter.getTitle;

import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 22, 2017
 *
 */
public class CalendarFolderHandler
{

    private Logger log = LogManager.getLogger(getClass());
    @PersistenceContext
    private EntityManager em;
    private String entityTypeForQuery;
    private String entityIdForQuery;

    /**
     * @param user
     * @param objectId
     * @param objectType
     * @param object
     * @throws CalendarServiceException
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String recreateFolder(AcmOutlookUser user, Long objectId, String objectType, CalendarFolderHandlerCallback callback)
            throws CalendarServiceException
    {
        try
        {
            TypedQuery<AcmEntity> query = em.createQuery(
                    String.format("SELECT obj FROM %s obj WHERE obj.%s = :id", entityTypeForQuery, entityIdForQuery), AcmEntity.class);
            query.setParameter("id", objectId);

            AcmEntity entity = query.getSingleResult();

            String folderName = String.format("%s(%s)", getTitle(entity), getNotifiableEntityTitle(entity));

            callback.callback(user, objectId, objectType, folderName, getContainer(entity), getParticipants(entity));

            return folderName;

        }
        catch (Exception e)
        {
            log.warn("Error creating query [{}] for object with id [{}] of [{}] type.",
                    String.format("SELECT obj FROM obj %s WHERE obj.%s = :id", entityTypeForQuery, entityIdForQuery), objectId, objectType,
                    e);
            throw new CalendarServiceException(e);
        }
    }

    /**
     * @param entityTypeForQuery
     *            the entityTypeForQuery to set
     */
    public void setEntityTypeForQuery(String entityTypeForQuery)
    {
        this.entityTypeForQuery = entityTypeForQuery;
    }

    /**
     * @param entityIdForQuery
     *            the entityIdForQuery to set
     */
    public void setEntityIdForQuery(String entityIdForQuery)
    {
        this.entityIdForQuery = entityIdForQuery;
    }

    @FunctionalInterface
    public static interface CalendarFolderHandlerCallback
    {

        void callback(AcmOutlookUser outlookUser, Long objectId, String objectType, String folderName, AcmContainer container,
                List<AcmParticipant> participants);

    }

}
