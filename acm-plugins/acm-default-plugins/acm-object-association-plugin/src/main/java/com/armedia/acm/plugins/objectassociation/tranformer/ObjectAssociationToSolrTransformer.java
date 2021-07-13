package com.armedia.acm.plugins.objectassociation.tranformer;

/*-
 * #%L
 * ACM Default Plugin: Object Associations
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
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;

import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by nebojsha.davidovikj on 06/17/17.
 */
public class ObjectAssociationToSolrTransformer implements AcmObjectToSolrDocTransformer<ObjectAssociation>
{
    private final Logger log = LogManager.getLogger(getClass());
    private ObjectAssociationDao objectAssociationDao;

    @Override
    public List<ObjectAssociation> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getObjectAssociationDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(ObjectAssociation in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        log.debug("Creating Solr advanced search document for REFERENCE.");

        mapRequiredProperties(solrDoc, in.getAssociationId(), in.getCreator(), in.getCreated(), in.getModifier(),
                in.getModified(), ObjectAssociationConstants.REFFERENCE_TYPE, null);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(ObjectAssociation in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(STATUS_LCS, in.getStatus()); // Reference status used by Bactes
        additionalProperties.put("association_type_s", in.getAssociationType());
        if (in.getInverseAssociation() != null)
        {
            additionalProperties.put("inverse_association_type_s", in.getInverseAssociation().getAssociationType());
            additionalProperties.put("inverse_association_id_s",
                    in.getInverseAssociation().getAssociationId() + "-" + ObjectAssociationConstants.REFFERENCE_TYPE);
        }
        else
        {
            additionalProperties.put("inverse_association_type_s", null);
            additionalProperties.put("inverse_association_id_s", null);
        }
        additionalProperties.put(PARENT_ID_S, in.getParentId());
        additionalProperties.put(PARENT_TYPE_S, in.getParentType());
        additionalProperties.put("parent_title_s", in.getParentName());
        additionalProperties.put(PARENT_REF_S, in.getParentId() + "-" + in.getParentType());

        additionalProperties.put("target_id_s", in.getTargetId());
        additionalProperties.put("target_type_s", in.getTargetType());
        additionalProperties.put("target_name_s", in.getTargetName());
        additionalProperties.put("target_title_s", in.getTargetTitle());
        additionalProperties.put("target_ref_s", in.getTargetId() + "-" + in.getTargetType());

        additionalProperties.put("description_s", in.getDescription());
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return ObjectAssociation.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return ObjectAssociation.class;
    }

    public ObjectAssociationDao getObjectAssociationDao()
    {
        return objectAssociationDao;
    }

    public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao)
    {
        this.objectAssociationDao = objectAssociationDao;
    }
}
