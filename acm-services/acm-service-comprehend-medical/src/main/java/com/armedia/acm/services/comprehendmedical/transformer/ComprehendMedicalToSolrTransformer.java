package com.armedia.acm.services.comprehendmedical.transformer;

/*-
 * #%L
 * ACM Service: Comprehend Medical
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TYPE_LCS;

import com.armedia.acm.services.comprehendmedical.dao.ComprehendMedicalDao;
import com.armedia.acm.services.comprehendmedical.model.ComprehendMedical;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 05/12/2020
 */
public class ComprehendMedicalToSolrTransformer implements AcmObjectToSolrDocTransformer<ComprehendMedical>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private ComprehendMedicalDao comprehendMedicalDao;

    @Override
    public List<ComprehendMedical> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getComprehendMedicalDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(ComprehendMedical in)
    {
        LOG.debug("Creating Solr advanced search document for COMPREHEND_MEDICAL.");

        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                in.getObjectType(), in.getMediaEcmFileVersion().getFile().getFileName());

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(ComprehendMedical in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TITLE_PARSEABLE, in.getMediaEcmFileVersion().getFile().getFileName());
        additionalProperties.put(TITLE_PARSEABLE_LCS, in.getMediaEcmFileVersion().getFile().getFileName());
        additionalProperties.put(STATUS_LCS, in.getStatus());
        additionalProperties.put(TYPE_LCS, in.getType());

        additionalProperties.put("remote_id_s", in.getRemoteId());
        additionalProperties.put("language_s", in.getLanguage());
        additionalProperties.put("media_file_version_id_l", in.getMediaEcmFileVersion().getId());

        additionalProperties.put("process_id_s", in.getProcessId());

        additionalProperties.put("parent_root_id_s", in.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        additionalProperties.put("parent_root_type_s", in.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        additionalProperties.put("parent_file_id_s", in.getMediaEcmFileVersion().getFile().getId());
        additionalProperties.put("title_t", in.getMediaEcmFileVersion().getFile().getFileName());
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return ComprehendMedical.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return ComprehendMedical.class;
    }

    public ComprehendMedicalDao getComprehendMedicalDao()
    {
        return comprehendMedicalDao;
    }

    public void setComprehendMedicalDao(ComprehendMedicalDao comprehendMedicalDao)
    {
        this.comprehendMedicalDao = comprehendMedicalDao;
    }
}
