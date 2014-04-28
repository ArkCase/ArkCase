package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.configuration.ListOfValuesService;
import com.armedia.acm.configuration.ListOfValuesType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class GetComplaintListOfValuesAPIController
{
    private ListOfValuesService listOfValuesService;

    @RequestMapping(
            value = "priorities",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<String> getComplaintPriorities()
    {
        List<String> priorities = getListOfValuesService().lookupListOfStringValues(ListOfValuesType.COMPLAINT_PRIORITY);
        return priorities;
    }

    @RequestMapping(
            value = "types",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<String> getComplaintTypes()
    {
        List<String> types = getListOfValuesService().lookupListOfStringValues(ListOfValuesType.COMPLAINT_TYPE);
        return types;
    }


    public ListOfValuesService getListOfValuesService()
    {
        return listOfValuesService;
    }

    public void setListOfValuesService(ListOfValuesService listOfValuesService)
    {
        this.listOfValuesService = listOfValuesService;
    }
}
