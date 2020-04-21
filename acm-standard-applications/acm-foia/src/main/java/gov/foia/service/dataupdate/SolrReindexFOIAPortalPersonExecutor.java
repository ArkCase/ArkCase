package gov.foia.service.dataupdate;

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import com.armedia.acm.services.dataupdate.service.SolrReindexService;

import java.util.Arrays;

import gov.foia.model.PortalFOIAPerson;

/**
 * Created by ana.serafimoska
 */
public class SolrReindexFOIAPortalPersonExecutor implements AcmDataUpdateExecutor
{
    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-foia-portal-person-reindex";
    }

    @Override
    public void execute()
    {
        solrReindexService.reindex(Arrays.asList(PortalFOIAPerson.class));
    }

    public SolrReindexService getSolrReindexService() {
        return solrReindexService;
    }

    public void setSolrReindexService(SolrReindexService solrReindexService) {
        this.solrReindexService = solrReindexService;
    }
}