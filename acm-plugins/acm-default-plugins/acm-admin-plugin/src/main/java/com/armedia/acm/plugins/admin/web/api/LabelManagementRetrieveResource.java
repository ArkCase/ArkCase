package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.services.labels.exception.AcmLabelManagementException;
import com.armedia.acm.services.labels.service.LabelManagementService;

import org.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by sergey on 2/14/16.
 */
@Controller
@RequestMapping({
        "/api/v1/plugin/admin",
        "/api/latest/plugin/admin" })
public class LabelManagementRetrieveResource
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private LabelManagementService labelManagementService;

    @RequestMapping(value = "/labelmanagement/resource", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public String retrieveResource(@RequestParam("lang") String lang, @RequestParam("ns") String ns, HttpServletResponse response)
            throws AcmLabelManagementException
    {
        return retrieveResourceJson(lang, ns).toString();
    }

    @RequestMapping(value = "/labelmanagement/resources", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveResources(
            @RequestParam("lang") String lang,
            @RequestParam("ns[]") String[] ns,
            HttpServletResponse response) throws AcmLabelManagementException
    {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < ns.length; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lang", lang);
            jsonObject.put("ns", ns[i]);
            jsonObject.put("res", retrieveResourceJson(lang, ns[i]));
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    private JSONObject retrieveResourceJson(String lang, String ns) throws AcmLabelManagementException
    {
        try
        {
            // Retrieve resource, third parameter indicates that we have to try create resource it it is absent
            JSONObject jsonResource = labelManagementService.getResource(ns, lang, true);

            // Return empty JSON if resource is absent
            if (jsonResource == null)
            {
                jsonResource = new JSONObject();
            }
            return jsonResource;
        } catch (Exception e)
        {
            String msg = String.format("Can't retrieve resource %s:%s", lang, ns);
            log.error(msg, e);
            throw new AcmLabelManagementException(msg, e);
        }
    }

    public void setLabelManagementService(LabelManagementService labelManagementService)
    {
        this.labelManagementService = labelManagementService;
    }

}