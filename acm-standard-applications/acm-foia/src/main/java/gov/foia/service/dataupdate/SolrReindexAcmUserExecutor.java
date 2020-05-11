package gov.foia.service.dataupdate;

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import com.armedia.acm.services.dataupdate.service.SolrReindexService;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Arrays;

/**
 * Created by ana.serafimoska
 */
public class SolrReindexAcmUserExecutor implements AcmDataUpdateExecutor
{
    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-user-reindex";
    }

    @Override
    public void execute()
    {
        solrReindexService.reindex(Arrays.asList(AcmUser.class));
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