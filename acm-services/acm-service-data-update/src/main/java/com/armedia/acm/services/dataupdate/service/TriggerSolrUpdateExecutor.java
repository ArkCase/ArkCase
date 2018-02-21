package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import java.util.Arrays;

public class TriggerSolrUpdateExecutor implements AcmDataUpdateExecutor
{

    private SolarReindexExecutor solarReindexExecutor;

    @Override
    public String getUpdateId()
    {
        return "solr-users-and-groups-update-v2";
    }

    @Override
    public void execute()
    {
        solarReindexExecutor.reindex(Arrays.asList(AcmUser.class, AcmGroup.class));
    }

    public SolarReindexExecutor getSolarReindexExecutor()
    {
        return solarReindexExecutor;
    }

    public void setSolarReindexExecutor(SolarReindexExecutor solarReindexExecutor)
    {
        this.solarReindexExecutor = solarReindexExecutor;
    }
}
