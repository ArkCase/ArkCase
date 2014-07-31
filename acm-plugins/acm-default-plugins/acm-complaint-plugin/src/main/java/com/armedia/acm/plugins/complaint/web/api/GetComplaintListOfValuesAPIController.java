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

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/complaint", "/api/latest/plugin/complaint" })
public class GetComplaintListOfValuesAPIController
{
    private ListOfValuesService listOfValuesService;

    private LookupTableDescriptor priorityDescriptor;
    private LookupTableDescriptor typesDescriptor;

    @RequestMapping(
            value = "priorities",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE })
    public @ResponseBody List<String> getComplaintPriorities() throws AcmListObjectsFailedException
    {
        try
        {
            List<String> priorities = getListOfValuesService().lookupListOfStringValues(getPriorityDescriptor());
            return priorities;
        }
        catch (DataAccessException e)
        {
            throw new AcmListObjectsFailedException("complaint priorities", e.getMessage(), e);
        }
    }

    @RequestMapping(
            value = "types",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<String> getComplaintTypes() throws AcmListObjectsFailedException
    {
        try
        {
            List<String> types = getListOfValuesService().lookupListOfStringValues(getTypesDescriptor());
            return types;
        }
        catch (DataAccessException e)
        {
            throw new AcmListObjectsFailedException("complaint types", e.getMessage(), e);
        }
    }


    public ListOfValuesService getListOfValuesService()
    {
        return listOfValuesService;
    }

    public void setListOfValuesService(ListOfValuesService listOfValuesService)
    {
        this.listOfValuesService = listOfValuesService;
    }

    public LookupTableDescriptor getPriorityDescriptor()
    {
        return priorityDescriptor;
    }

    public void setPriorityDescriptor(LookupTableDescriptor priorityDescriptor)
    {
        this.priorityDescriptor = priorityDescriptor;
    }

    public LookupTableDescriptor getTypesDescriptor()
    {
        return typesDescriptor;
    }

    public void setTypesDescriptor(LookupTableDescriptor typesDescriptor)
    {
        this.typesDescriptor = typesDescriptor;
    }
}
