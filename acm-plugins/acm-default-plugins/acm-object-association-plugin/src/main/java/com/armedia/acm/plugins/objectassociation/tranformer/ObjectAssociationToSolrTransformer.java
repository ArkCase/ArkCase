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

import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationConstants;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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

        solrDoc.setId(in.getAssociationId() + "-" + ObjectAssociationConstants.REFFERENCE_TYPE);
        solrDoc.setObject_type_s(ObjectAssociationConstants.REFFERENCE_TYPE);
        solrDoc.setObject_id_s(in.getAssociationId() + "");

        solrDoc.setCreate_date_tdt(in.getCreated());
        solrDoc.setCreator_lcs(in.getCreator());
        solrDoc.setModified_date_tdt(in.getModified());
        solrDoc.setModifier_lcs(in.getModifier());
        solrDoc.setStatus_lcs(in.getStatus()); // Reference status used by Bactes

        setAdditionalProperties(solrDoc.getAdditionalProperties(), in);

        return solrDoc;
    }

    @Override
    public SolrDocument toSolrQuickSearch(ObjectAssociation in)
    {
        SolrDocument solrDoc = new SolrDocument();
        solrDoc.setId(in.getAssociationId() + "-" + ObjectAssociationConstants.REFFERENCE_TYPE);
        solrDoc.setObject_type_s(ObjectAssociationConstants.REFFERENCE_TYPE);
        solrDoc.setObject_id_s(in.getAssociationId() + "");

        solrDoc.setCreate_tdt(in.getCreated());
        solrDoc.setAuthor_s(in.getCreator());
        solrDoc.setLast_modified_tdt(in.getModified());
        solrDoc.setModifier_s(in.getModifier());

        setAdditionalProperties(solrDoc.getAdditionalProperties(), in);

        return solrDoc;
    }

    private void setAdditionalProperties(Map<String, Object> additionalProperties, ObjectAssociation objectAssociation)
    {
        additionalProperties.put("association_type_s", objectAssociation.getAssociationType());
        if (objectAssociation.getInverseAssociation() != null)
        {
            additionalProperties.put("inverse_association_type_s", objectAssociation.getInverseAssociation().getAssociationType());
            additionalProperties.put("inverse_association_id_s",
                    objectAssociation.getInverseAssociation().getAssociationId() + "-" + ObjectAssociationConstants.REFFERENCE_TYPE);
        }
        else
        {
            additionalProperties.put("inverse_association_type_s", null);
            additionalProperties.put("inverse_association_id_s", null);
        }
        additionalProperties.put("parent_id_s", objectAssociation.getParentId());
        additionalProperties.put("parent_type_s", objectAssociation.getParentType());
        additionalProperties.put("parent_title_s", objectAssociation.getParentName());
        additionalProperties.put("parent_ref_s", objectAssociation.getParentId() + "-" + objectAssociation.getParentType());

        additionalProperties.put("target_id_s", objectAssociation.getTargetId());
        additionalProperties.put("target_type_s", objectAssociation.getTargetType());
        additionalProperties.put("target_name_s", objectAssociation.getTargetName());
        additionalProperties.put("target_title_s", objectAssociation.getTargetTitle());
        additionalProperties.put("target_ref_s", objectAssociation.getTargetId() + "-" + objectAssociation.getTargetType());

        additionalProperties.put("description_s", objectAssociation.getDescription());
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
