package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.service.solr.SolrPostClient;
import com.armedia.acm.services.search.service.solr.SolrPostException;
import com.armedia.acm.services.search.service.solr.SolrRestClient;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.Objects;
import java.util.UUID;

public class ContentFileSolrPostClient implements SolrPostClient
{

    private transient final Logger logger = LoggerFactory.getLogger(getClass());
    private ObjectConverter objectConverter;
    private EcmFileService ecmFileService;
    private CmisConfigUtils cmisConfigUtils;
    private SolrRestClient solrRestClient;
    private String solrContentFileHandler;

    @Override
    public void sendToSolr(SolrCore core, String json) throws SolrPostException
    {
        Objects.requireNonNull(core, "Core must be specified");
        Objects.requireNonNull(core.getCore(), "The Solr core must have a value");
        Objects.requireNonNull(json, "JSON must be specified");

        final SolrContentDocument solrContentDocument = getObjectConverter().getJsonUnmarshaller().unmarshall(json,
                SolrContentDocument.class);
        final String cmisRepositoryId = (String) solrContentDocument.getAdditionalProperties().get("cmis_repository_id_s");
        final String cmisObjectId = solrContentDocument.getCmis_version_series_id_s();

        logger.debug("Finding content file {} from repository id: {}", cmisObjectId, cmisRepositoryId);

        storeCmisUserId(cmisRepositoryId);

        Document cmisDoc = null;

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

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", solrContentDocument.getContent_type());
        InputStreamResource inputStreamResource = new InputStreamResource(contentStream.getStream());
        HttpEntity<InputStreamResource> entity = new HttpEntity<>(inputStreamResource, headers);

        getSolrRestClient().postToSolr(core.getCore(), getSolrContentFileHandler(), entity, logText, solrContentDocument.getUrl());

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
        CMISCloudConnectorConnectionManager cmisConfig = getCmisConfigUtils().getCmisConfiguration(cmisRepositoryId);
        boolean foundGoodCmisConfig = cmisConfig != null && cmisConfig.getUsername() != null && !cmisConfig.getUsername().trim().isEmpty();
        String cmisUser = foundGoodCmisConfig ? cmisConfig.getUsername() : "admin";
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

    public String getSolrContentFileHandler()
    {
        return solrContentFileHandler;
    }

    public void setSolrContentFileHandler(String solrContentFileHandler)
    {
        this.solrContentFileHandler = solrContentFileHandler;
    }

    public SolrRestClient getSolrRestClient()
    {
        return solrRestClient;
    }

    public void setSolrRestClient(SolrRestClient solrRestClient)
    {
        this.solrRestClient = solrRestClient;
    }

}
