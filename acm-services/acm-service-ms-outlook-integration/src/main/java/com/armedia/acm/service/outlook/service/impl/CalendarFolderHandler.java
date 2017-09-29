package com.armedia.acm.service.outlook.service.impl;

import static com.armedia.acm.service.outlook.service.impl.AcmEntityAdapter.getContainer;
import static com.armedia.acm.service.outlook.service.impl.AcmEntityAdapter.getNotifiableEntityTitle;
import static com.armedia.acm.service.outlook.service.impl.AcmEntityAdapter.getParticipants;
import static com.armedia.acm.service.outlook.service.impl.AcmEntityAdapter.getTitle;

import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @FunctionalInterface
    public static interface CalendarFolderHandlerCallback
    {

        void callback(AcmOutlookUser outlookUser, Long objectId, String objectType, String folderName, AcmContainer container,
                List<AcmParticipant> participants);

    }

    private Logger log = LoggerFactory.getLogger(getClass());

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

        } catch (Exception e)
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

}
