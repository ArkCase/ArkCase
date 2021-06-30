package com.armedia.acm.plugins.ecm.service.impl;

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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CMIS_VERSION_SERIES_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CONTENT_TYPE;

import com.armedia.acm.camelcontext.configuration.ArkCaseCMISConfig;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.model.EcmFileContentIndexedEvent;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.solr.SolrPostClient;
import com.armedia.acm.services.search.service.solr.SolrPostException;
import com.armedia.acm.services.search.service.solr.SolrRestClient;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ContentFileSolrPostClient implements SolrPostClient, ApplicationEventPublisherAware
{
    private transient final Logger logger = LogManager.getLogger(getClass());
    private ObjectConverter objectConverter;
    private EcmFileService ecmFileService;
    private CmisConfigUtils cmisConfigUtils;
    private SolrRestClient solrRestClient;
    private ApplicationEventPublisher applicationEventPublisher;
    private CamelContextManager camelContextManager;

    @Override
    public void sendToSolr(String destinationQueue, SolrCore core, String json) throws SolrPostException
    {
        Objects.requireNonNull(core, "Core must be specified");
        Objects.requireNonNull(core.getCore(solrRestClient.getSolrConfig()), "The Solr core must have a value");
        Objects.requireNonNull(json, "JSON must be specified");

        final SolrContentDocument solrContentDocument = getObjectConverter().getJsonUnmarshaller().unmarshall(json,
                SolrContentDocument.class);
        final String cmisRepositoryId = (String) solrContentDocument.getAdditionalProperties().get("cmis_repository_id_s");
        final String cmisObjectId = (String) solrContentDocument.getAdditionalProperties().get(CMIS_VERSION_SERIES_ID_S);

        logger.debug("Finding content file {} from repository id: {}", cmisObjectId, cmisRepositoryId);

        storeCmisUserId(cmisRepositoryId);

        Document cmisDoc = null;

        String urlWithPlaceholders = solrContentDocument.buildUrlTemplate();
        Map<String, Object> urlValues = solrContentDocument.buildUrlValues();

        try
        {
            cmisDoc = (Document) getEcmFileService().findObjectById(cmisRepositoryId, cmisObjectId);
        }
        catch (Exception e)
        {
            logger.error("Could not lookup the document [{}] from the CMIS repository [{}] {}", cmisObjectId, cmisRepositoryId,
                    e.getMessage(), e);
            throw new RuntimeException(e);
        }
        logger.debug("Found content file {} from repository id: {}", cmisObjectId, cmisRepositoryId);
        ContentStream contentStream = cmisDoc.getContentStream();

        final String logText = solrContentDocument.getName();
        final String contentType = solrContentDocument.getAdditionalProperties().get(CONTENT_TYPE).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", contentType);
        InputStreamResource inputStreamResource = new InputStreamResource(contentStream.getStream());
        HttpEntity<InputStreamResource> entity = new HttpEntity<>(inputStreamResource, headers);

        getSolrRestClient().postToSolr(destinationQueue, core.getCore(solrRestClient.getSolrConfig()),
                solrRestClient.getSolrConfig().getContentFileHandler(), entity,
                logText,
                urlWithPlaceholders, urlValues);

        applicationEventPublisher.publishEvent(new EcmFileContentIndexedEvent(solrContentDocument));

    }

    /**
     * For files that were just created, we could dependably use the file creator as the user name.
     * But we may be re-indexing long after the original users left the company, so their user IDs may
     * not be valid any more. So we will use the configured user id for the CMIS repository.
     * 
     * @param cmisRepositoryId
     */
    private void storeCmisUserId(final String cmisRepositoryId)
    {
        ArkCaseCMISConfig config = getCamelContextManager().getRepositoryConfigs().get(cmisRepositoryId);
        boolean foundGoodCmisConfig = config != null && config.getUsername() != null && !config.getUsername().trim().isEmpty();
        String cmisUser = foundGoodCmisConfig ? config.getUsername() : "admin";

        logger.debug("found a good cmis user id? {}, in any case, our CMIS user id is {}", foundGoodCmisConfig, cmisUser);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, cmisUser);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
    }

    public SolrRestClient getSolrRestClient()
    {
        return solrRestClient;
    }

    public void setSolrRestClient(SolrRestClient solrRestClient)
    {
        this.solrRestClient = solrRestClient;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public CamelContextManager getCamelContextManager()
    {
        return camelContextManager;
    }

    public void setCamelContextManager(CamelContextManager camelContextManager)
    {
        this.camelContextManager = camelContextManager;
    }
}
