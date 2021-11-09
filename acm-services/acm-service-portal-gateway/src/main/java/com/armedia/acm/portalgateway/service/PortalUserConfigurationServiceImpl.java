package com.armedia.acm.portalgateway.service;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.portalgateway.model.PortalUserConfig;

public class PortalUserConfigurationServiceImpl implements PortalUserConfigurationService {

    private PortalUserConfig portalUserConfig;


    @Override
    public PortalUserConfig getPortalUserConfiguration()
    {
        return portalUserConfig;
    }

    public void setPortalUserConfig(PortalUserConfig portalUserConfig)
    {
        this.portalUserConfig = portalUserConfig;
    }

}
