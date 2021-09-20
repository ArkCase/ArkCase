package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.plugins.consultation.model.Consultation;

import java.util.Arrays;

public class SolrReindexConsultationExecutor implements AcmDataUpdateExecutor {

    private SolrReindexService solrReindexService;
    @Override
    public String getUpdateId()
    {
        return "solr_consultation_reindex_v1";
    }

    @Override
    public void execute()
    {
        solrReindexService.reindex(Arrays.asList(Consultation.class));
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


