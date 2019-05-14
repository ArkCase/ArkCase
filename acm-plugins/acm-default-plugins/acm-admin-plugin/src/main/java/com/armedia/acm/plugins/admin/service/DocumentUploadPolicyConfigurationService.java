package com.armedia.acm.plugins.admin.service;

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.plugins.admin.model.DocumentUploadPolicyConfig;


import java.util.Map;

public class DocumentUploadPolicyConfigurationService
{

    private DocumentUploadPolicyConfig documentUploadPolicyConfig;
    private ConfigurationPropertyService configurationPropertyService;

    public Map<String, Object> getDocumentUploadPolicyConfig()
    {
        return getConfigurationPropertyService().getProperties(documentUploadPolicyConfig);
    }

    public void saveDocumentUploadPolicyConfig(DocumentUploadPolicyConfig documentUploadPolicyConfig)
    {
        getConfigurationPropertyService().updateProperties(documentUploadPolicyConfig);
    }

    public void setDocumentUploadPolicyConfig(DocumentUploadPolicyConfig documentUploadPolicyConfig)
    {
        this.documentUploadPolicyConfig = documentUploadPolicyConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService() {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
