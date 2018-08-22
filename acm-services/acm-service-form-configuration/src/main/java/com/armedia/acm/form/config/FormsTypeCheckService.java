package com.armedia.acm.form.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormsTypeCheckService
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private FormsTypeManagementService formsTypeManagementService;

    public String getTypeOfForm()
    {
        String formsType = "";
        try
        {
            formsType = formsTypeManagementService.getProperty("formsType").get("formsType").toString();
        }
        catch (Exception e)
        {
            String msg = "Can't retrieve application property";
            log.error(msg, e);
        }
        return formsType;
    }

    public void setFormsTypeManagementService(FormsTypeManagementService formsTypeManagementService)
    {
        this.formsTypeManagementService = formsTypeManagementService;
    }
}
