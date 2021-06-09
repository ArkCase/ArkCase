package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class NotificationToSolrTransformer implements AcmObjectToSolrDocTransformer<Notification>
{

    private final Logger LOG = LogManager.getLogger(getClass());

    private NotificationDao notificationDao;
    private UserDao userDao;
    private NotificationUtils notificationUtils;

    @Override
    public List<Notification> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getNotificationDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Notification in)
    {
        LOG.debug("Creating Solr advanced search document for Notification.");

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-" + NotificationConstants.OBJECT_TYPE);
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_type_s(NotificationConstants.OBJECT_TYPE);
        solr.setTitle_parseable(in.getTitle());
        solr.setTitle_parseable_lcs(in.getTitle());
        solr.setParent_id_s(Long.toString(in.getParentId()));
        solr.setParent_type_s(in.getParentType());
        solr.setParent_number_lcs(in.getParentName());
        solr.setParent_name_t(in.getParentTitle());
        solr.setOwner_lcs(in.getUser());
        solr.setDescription_parseable(in.getNote());
        solr.setState_lcs(in.getState());
        solr.setAction_lcs(in.getAction());
        solr.setData_lcs(in.getData());
        solr.setNotification_type_lcs(in.getType());

        solr.setParent_ref_s(in.getParentId() + "-" + in.getParentType());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setStatus_lcs(in.getStatus());

        solr.setType_lcs(in.getType());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        mapAdditionalProperties(in, solr.getAdditionalProperties());

        return solr;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer#isAcmObjectTypeSupported(java.lang.Class)
     */
    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Notification.class.equals(acmObjectType);
    }

    private void mapAdditionalProperties(Notification in, Map<String, Object> additionalProperties)
    {
        Long relatedObjectId = in.getRelatedObjectId();
        String relatedObjectType = in.getRelatedObjectType();
        String relatedObjectNumber = in.getRelatedObjectNumber();
        Date actionDate = in.getActionDate();
        String notificationLink = getNotificationUtils().buildNotificationLink(in.getParentType(), in.getParentId(),
                relatedObjectType, relatedObjectId);
        additionalProperties.put("related_object_id_l", relatedObjectId);
        additionalProperties.put("related_object_type_s", relatedObjectType);
        additionalProperties.put("related_object_number_s", relatedObjectNumber);
        additionalProperties.put("action_date_tdt", actionDate);
        additionalProperties.put("notification_link_s", notificationLink);
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return Notification.class;
    }

    public NotificationUtils getNotificationUtils()
    {
        return notificationUtils;
    }

    public void setNotificationUtils(NotificationUtils notificationUtils)
    {
        this.notificationUtils = notificationUtils;
    }
}
