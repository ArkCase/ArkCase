package com.armedia.acm.service.objectlock.transformer;

/*-
 * #%L
 * ACM Service: Object lock
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;

import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by nebojsha on 21.08.2015.
 */
public class AcmObjectLockToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmObjectLock>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private AcmObjectLockDao dao;
    private UserDao userDao;

    @Override
    public List<AcmObjectLock> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmObjectLock in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for OBJECT_LOCK.");

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                "OBJECT_LOCK", null);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(AcmObjectLock in, Map<String, Object> additionalProperties)
    {

        additionalProperties.put(PARENT_ID_S, in.getObjectId().toString());
        additionalProperties.put(PARENT_TYPE_S, in.getObjectType());
        additionalProperties.put(PARENT_REF_S, in.getObjectId() + "-" + in.getObjectType());

        /** Add partial creator username field **/
        additionalProperties.put("creator_partial", in.getCreator());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            additionalProperties.put(CREATOR_FULL_NAME_LCS, creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            additionalProperties.put(MODIFIER_FULL_NAME_LCS, modifier.getFirstName() + " " + modifier.getLastName());
        }

        additionalProperties.put("expiry_tdt", in.getExpiry());
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmObjectLock.class.equals(acmObjectType);
    }

    public AcmObjectLockDao getDao()
    {
        return dao;
    }

    public void setDao(AcmObjectLockDao dao)
    {
        this.dao = dao;
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
        return AcmObjectLock.class;
    }
}
