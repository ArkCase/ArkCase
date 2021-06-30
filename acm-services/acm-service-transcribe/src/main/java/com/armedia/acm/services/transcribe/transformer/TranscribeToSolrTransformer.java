package com.armedia.acm.services.transcribe.transformer;

/*-
 * #%L
 * ACM Service: Transcribe
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

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.transcribe.dao.TranscribeDao;
import com.armedia.acm.services.transcribe.model.Transcribe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
public class TranscribeToSolrTransformer implements AcmObjectToSolrDocTransformer<Transcribe>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private TranscribeDao transcribeDao;

    @Override
    public List<Transcribe> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getTranscribeDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Transcribe in)
    {
        LOG.debug("Creating Solr advanced search document for Transcribe.");

        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                in.getObjectType(), in.getMediaEcmFileVersion().getFile().getFileName());

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(Transcribe in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TITLE_PARSEABLE, in.getMediaEcmFileVersion().getFile().getFileName());
        additionalProperties.put(TITLE_PARSEABLE_LCS, in.getMediaEcmFileVersion().getFile().getFileName());
        additionalProperties.put("title_t", in.getMediaEcmFileVersion().getFile().getFileName());
        additionalProperties.put(STATUS_LCS, in.getStatus());
        additionalProperties.put(TYPE_LCS, in.getType());

        additionalProperties.put("remote_id_s", in.getRemoteId());
        additionalProperties.put("language_s", in.getLanguage());
        additionalProperties.put("media_file_version_id_l", in.getMediaEcmFileVersion().getId());

        additionalProperties.put("process_id_s", in.getProcessId());
        additionalProperties.put("word_count_l", in.getWordCount());
        additionalProperties.put("confidence_l", in.getConfidence());

        additionalProperties.put("parent_root_id_s", in.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        additionalProperties.put("parent_root_type_s", in.getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        additionalProperties.put("parent_file_id_s", in.getMediaEcmFileVersion().getFile().getId());
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Transcribe.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return Transcribe.class;
    }

    public TranscribeDao getTranscribeDao()
    {
        return transcribeDao;
    }

    public void setTranscribeDao(TranscribeDao transcribeDao)
    {
        this.transcribeDao = transcribeDao;
    }
}
