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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_NAME_T;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_NUMBER_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_REF_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE_LCS;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.transcribe.dao.TranscribeItemDao;
import com.armedia.acm.services.transcribe.model.TranscribeItem;
import com.armedia.acm.services.transcribe.utils.TranscribeUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
public class TranscribeItemToSolrTransformer implements AcmObjectToSolrDocTransformer<TranscribeItem>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private TranscribeItemDao transcribeItemDao;

    @Override
    public List<TranscribeItem> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getTranscribeItemDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(TranscribeItem in)
    {
        LOG.debug("Creating Solr advanced search document for TranscribeItem.");

        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();

        String name = TranscribeUtils.getFirstWords(in.getText(), 5);
        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                in.getObjectType(), name);

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(TranscribeItem in, Map<String, Object> additionalProperties)
    {
        String name = TranscribeUtils.getFirstWords(in.getText(), 5);
        additionalProperties.put(TITLE_PARSEABLE, name);
        additionalProperties.put(TITLE_PARSEABLE_LCS, name);
        additionalProperties.put(PARENT_ID_S, String.valueOf(in.getTranscribe().getId()));
        additionalProperties.put(PARENT_NUMBER_LCS, in.getTranscribe().getObjectType());
        additionalProperties.put(PARENT_NAME_T, in.getTranscribe().getMediaEcmFileVersion().getFile().getFileName());
        additionalProperties.put(PARENT_REF_S, String.format("%d-%s", in.getTranscribe().getId(), in.getTranscribe().getObjectType()));

        additionalProperties.put("start_time_s", in.getStartTime().toString());
        additionalProperties.put("end_time_s", in.getEndTime().toString());
        additionalProperties.put("confidence_l", in.getConfidence());
        additionalProperties.put("corrected_b", in.getCorrected().booleanValue());
        additionalProperties.put("text_s", in.getText());

        additionalProperties.put("parent_root_id_s",
                in.getTranscribe().getMediaEcmFileVersion().getFile().getContainer().getContainerObjectId());
        additionalProperties.put("parent_root_type_s",
                in.getTranscribe().getMediaEcmFileVersion().getFile().getContainer().getContainerObjectType());
        additionalProperties.put("parent_file_id_s", in.getTranscribe().getMediaEcmFileVersion().getFile().getId());
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return TranscribeItem.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return TranscribeItem.class;
    }

    public TranscribeItemDao getTranscribeItemDao()
    {
        return transcribeItemDao;
    }

    public void setTranscribeItemDao(TranscribeItemDao transcribeItemDao)
    {
        this.transcribeItemDao = transcribeItemDao;
    }
}
