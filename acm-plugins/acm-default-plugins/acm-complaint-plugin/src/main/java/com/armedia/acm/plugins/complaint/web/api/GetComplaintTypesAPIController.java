package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.pluginmanager.AcmPlugin;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class GetComplaintTypesAPIController
{
    private AcmPlugin complaintPlugin;

    @RequestMapping(
            value = "types",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String[] getComplaintTypes()
    {
        String typeCsv = (String) getComplaintPlugin().getPluginProperties().get("complaint.complaintTypes");
        String[] types = typeCsv.split(",");
        return types;
    }


    public AcmPlugin getComplaintPlugin()
    {
        return complaintPlugin;
    }

    public void setComplaintPlugin(AcmPlugin complaintPlugin)
    {
        this.complaintPlugin = complaintPlugin;
    }
}
