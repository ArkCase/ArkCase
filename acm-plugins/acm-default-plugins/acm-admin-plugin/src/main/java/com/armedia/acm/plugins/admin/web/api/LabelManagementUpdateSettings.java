package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelManagementException;
import com.armedia.acm.plugins.admin.service.LabelManagementService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by sergey on 2/14/16.
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class LabelManagementUpdateSettings
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private LabelManagementService labelManagementService;

    @RequestMapping(value = "/labelmanagement/settings", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String updateSettings(@RequestBody String settings) throws AcmLabelManagementException
    {

        try
        {
            JSONObject settingsObj = new JSONObject(settings);
            JSONObject updatedSettingsObj = labelManagementService.updateSettings(settingsObj);
            return updatedSettingsObj.toString();
        } catch (Exception e)
        {
            String msg = "Can't update setitngs";
            log.error(msg, e);
            throw new AcmLabelManagementException(msg, e);
        }
    }


    public void setLabelManagementService(LabelManagementService labelManagementService)
    {
        this.labelManagementService = labelManagementService;
    }
}
