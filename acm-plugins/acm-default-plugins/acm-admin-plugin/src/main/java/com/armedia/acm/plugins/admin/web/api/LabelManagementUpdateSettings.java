package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelManagementException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sergey on 2/14/16.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LabelManagementUpdateSettings {
    private Logger log = LoggerFactory.getLogger(getClass());
    private LabelManagementService labelManagementService;

    @RequestMapping(value = "/labelmanagement/settings", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public void updateSettings(@RequestBody String settings) throws AcmLabelManagementException {

        try {
            JSONObject settingsObj = new JSONObject(settings);
            labelManagementService.updateSettings(settingsObj);
        } catch (Exception e){
            String errMsg = String.format("Can't update setitngs");
            if (log.isErrorEnabled()) {
                log.error(String.format(errMsg, e));
            }
            throw new AcmLabelManagementException(errMsg, e);
        }
    }


    public void setLabelManagementService(LabelManagementService labelManagementService) {
        this.labelManagementService = labelManagementService;
    }
}
