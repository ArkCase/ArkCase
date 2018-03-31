package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.costsheet.model.AcmCostsheet;

import java.util.Arrays;

public class TriggerCostsheetSolrUpdateExecutor implements AcmDataUpdateExecutor
{
    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-costsheet-update-v1";
    }

    @Override
    public void execute()
    {
        solrReindexService.reindex(Arrays.asList(AcmCostsheet.class));
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
