package gov.foia.service.dataupdate;

import com.armedia.acm.services.dataupdate.service.AcmDataUpdateExecutor;
import com.armedia.acm.services.dataupdate.service.SolrReindexService;
import com.armedia.acm.services.notification.model.Notification;

import java.util.Arrays;

public class FoiaNotificationDataUpdateExecutor implements AcmDataUpdateExecutor
{

    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "solr-notification-update";
    }

    @Override
    public void execute()
    {
        solrReindexService.reindex(Arrays.asList(Notification.class));
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
