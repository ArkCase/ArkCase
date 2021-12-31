package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_OBJECT_ID_I;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;

import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 3/23/15.
 */
public class AcmContainerToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmContainer>
{
    private AcmContainerDao dao;
    private final Logger LOG = LogManager.getLogger(getClass());

    @Override
    public List<AcmContainer> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmContainer in)
    {

        SolrAdvancedSearchDocument doc = new SolrAdvancedSearchDocument();

        LOG.debug("Creating Solr advanced search document for CONTAINER.");

        mapRequiredProperties(doc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(), in.getObjectType(),
                in.getContainerObjectTitle());

        // no access control on folders (yet)
        doc.setPublic_doc_b(true);

        mapAdditionalProperties(in, doc.getAdditionalProperties());

        return doc;
    }

    @Override
    public void mapAdditionalProperties(AcmContainer in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TITLE_PARSEABLE, in.getContainerObjectTitle());
        additionalProperties.put(TITLE_PARSEABLE_LCS, "" + in.getContainerObjectTitle());
        additionalProperties.put(PARENT_OBJECT_ID_I, in.getContainerObjectId());
        additionalProperties.put(PARENT_ID_S, "" + in.getContainerObjectId());
        additionalProperties.put(PARENT_TYPE_S, in.getContainerObjectType());

        // folder id will be used to find files and folders that belong to this container
        if (in.getFolder() != null)
        {
            additionalProperties.put("folder_id_i", in.getFolder().getId());
            additionalProperties.put("folder_name_s", in.getFolder().getName());
        }
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmContainer.class.equals(acmObjectType);
    }

    public AcmContainerDao getDao()
    {
        return dao;
    }

    public void setDao(AcmContainerDao dao)
    {
        this.dao = dao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmContainer.class;
    }
}
