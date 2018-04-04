package com.armedia.acm.plugins.ecm.service.impl;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.service.solr.SolrPostClient;
import com.armedia.acm.services.search.service.solr.SolrPostException;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;

import java.util.UUID;

/**
 * Created by david.miller on 2018-04-04.
 */
public class SolrPostContentFileQueueListener
{
    private transient final Logger logger = LoggerFactory.getLogger(getClass());

    private SolrPostClient solrPostClient;
    private ObjectConverter objectConverter;
    private EcmFileService ecmFileService;
    private CmisConfigUtils cmisConfigUtils;

    public SolrPostContentFileQueueListener()
    {
        logger.debug("SolrPostContentFileQueueListener is up and running");
    }

    @JmsListener(destination = "solrContentFile.in", containerFactory = "jmsListenerContainerFactory")
    public void onContentFilePost(String jsonDocument)
    {
        logger.debug("handling a message from solrContentFile.in");
        try
        {
            final SolrContentDocument solrContentDocument = getObjectConverter().getJsonUnmarshaller().unmarshall(jsonDocument,
                    SolrContentDocument.class);
            final String cmisRepositoryId = (String) solrContentDocument.getAdditionalProperties().get("cmis_repository_id_s");
            final String cmisObjectId = solrContentDocument.getCmis_version_series_id_s();

            logger.debug("Finding content file {} from repository id: {}", cmisObjectId, cmisRepositoryId);

            storeCmisUserId(cmisRepositoryId);

            Document cmisDoc = (Document) getEcmFileService().findObjectById(cmisRepositoryId, cmisObjectId);
            logger.debug("Found content file {} from repository id: {}", cmisObjectId, cmisRepositoryId);
            ContentStream contentStream = cmisDoc.getContentStream();

            getSolrPostClient().sendToSolr(SolrCore.ADVANCED_SEARCH, solrContentDocument, contentStream.getStream(),
                    cmisDoc.getContentStreamLength());
        }
        catch (SolrPostException e)
        {
            logger.error("Could not post to Solr: {}", e.getMessage(), e);
        }
        catch (Exception e)
        {
            logger.error("Could not find document to send to Solr: {}", e.getMessage(), e);
        }
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

    public SolrPostClient getSolrPostClient()
    {
        return solrPostClient;
    }

    public void setSolrPostClient(SolrPostClient solrPostClient)
    {
        this.solrPostClient = solrPostClient;
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

}
