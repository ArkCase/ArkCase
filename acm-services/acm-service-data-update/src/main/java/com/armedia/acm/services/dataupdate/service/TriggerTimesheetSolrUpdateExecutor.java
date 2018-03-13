package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.timesheet.model.AcmTimesheet;

import java.util.Arrays;

public class TriggerTimesheetSolrUpdateExecutor implements AcmDataUpdateExecutor
{
    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-timesheet-update-v1";
    }

    @Override
    public void execute()
    {
        solrReindexService.reindex(Arrays.asList(AcmTimesheet.class));
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
