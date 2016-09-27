package com.armedia.acm.services.notification.service;

import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class NotificationToSolrTransformer implements AcmObjectToSolrDocTransformer<Notification>
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

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

    @Override
    public SolrDocument toSolrQuickSearch(Notification in)
    {
        LOG.info("Creating Solr quick search document for Notification.");

        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + NotificationConstants.OBJECT_TYPE);
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_type_s(NotificationConstants.OBJECT_TYPE);
        solr.setTitle_parseable(in.getTitle());

        solr.setParent_ref_s(in.getParentId() + "-" + in.getParentType());

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setNotification_type_s(in.getType());
        solr.setData_s(in.getData());

        solr.setStatus_s(in.getStatus());

        solr.setType_s(in.getType());

        mapAdditionalProperties(in, solr.getAdditionalProperties());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(Notification in)
    {
        // No implementation needed
        return null;
    }

    /*
     * (non-Javadoc)
     *
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
        String notificationLink = getNotificationUtils().buildNotificationLink(in.getParentType(), in.getParentId(),
                relatedObjectType, relatedObjectId);
        additionalProperties.put("related_object_id_l", relatedObjectId);
        additionalProperties.put("related_object_type_s", relatedObjectType);
        additionalProperties.put("related_object_number_s", relatedObjectNumber);
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
