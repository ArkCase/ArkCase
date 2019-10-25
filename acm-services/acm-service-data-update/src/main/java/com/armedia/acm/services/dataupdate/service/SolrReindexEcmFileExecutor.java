package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author aleksandar.bujaroski
 */
public class SolrReindexEcmFileExecutor implements AcmDataUpdateExecutor
{

    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-ecm-files-reindex-1";
    }

    @Override
    public void execute()
    {
        getSolrReindexService().reindex(new ArrayList<>(Arrays.asList(EcmFile.class)));
    }

    public SolrReindexService getSolrReindexService()
    {
        return solrReindexService;
    }

    public void setSolrReindexService(SolrReindexService solrReindexService)
    {
        this.solrReindexService = solrReindexService;
    }
}
