package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.configuration.ListOfValuesService;
import com.armedia.acm.configuration.LookupTableDescriptor;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class GetComplaintListOfValuesAPIController
{

    private Properties complaintProperties;

    @RequestMapping(
            value = "priorities",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE })
    public @ResponseBody List<String> getComplaintPriorities() throws AcmListObjectsFailedException
    {
        String commaSeparated = getComplaintProperties().getProperty("complaint.priorities");

        String[] retval = commaSeparated.split(",");

        return Arrays.asList(retval);
    }

    @RequestMapping(
            value = "types",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<String> getComplaintTypes() throws AcmListObjectsFailedException
    {

        String commaSeparated = getComplaintProperties().getProperty("complaint.complaint-types");

        String[] retval = commaSeparated.split(",");

        return Arrays.asList(retval);

    }

    public Properties getComplaintProperties()
    {
        return complaintProperties;
    }

    public void setComplaintProperties(Properties complaintProperties)
    {
        this.complaintProperties = complaintProperties;
    }
}
