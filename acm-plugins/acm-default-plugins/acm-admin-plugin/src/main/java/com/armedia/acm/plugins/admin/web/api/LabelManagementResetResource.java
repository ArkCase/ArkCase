package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLabelManagementException;
import com.armedia.acm.plugins.admin.service.LabelManagementService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey on 2/10/16.
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class LabelManagementResetResource
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private LabelManagementService labelManagementService;

    @RequestMapping(value = "/labelmanagement/admin-resource/reset", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String updateResource(@RequestBody String resource) throws IOException, AcmLabelManagementException
    {

        List<String> ns = new ArrayList<>();
        List<String> langs = new ArrayList<>();

        try
        {
            JSONObject resourceObject = new JSONObject(resource);
            JSONArray nsJsonArray = resourceObject.getJSONArray("ns");
            for (int i = 0; i < nsJsonArray.length(); i++)
            {
                ns.add(nsJsonArray.getString(i));
            }
            JSONArray langJsonArray = resourceObject.getJSONArray("lng");
            for (int i = 0; i < langJsonArray.length(); i++)
            {
                langs.add(langJsonArray.getString(i));
            }
        } catch (Exception e)
        {
            log.error(String.format("Wrong reset parameter '%s' ", resource));
            throw new AcmLabelManagementException("Reset resource error", e);
        }

        labelManagementService.reset(ns, langs);
        return (new JSONObject()).toString();
    }

    public void setLabelManagementService(LabelManagementService labelManagementService)
    {
        this.labelManagementService = labelManagementService;
    }

}
