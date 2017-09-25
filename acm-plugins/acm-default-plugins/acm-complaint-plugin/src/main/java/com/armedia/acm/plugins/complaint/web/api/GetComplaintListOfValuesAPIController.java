package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Controller
@RequestMapping({
        "/api/v1/plugin/complaint",
        "/api/latest/plugin/complaint" })
public class GetComplaintListOfValuesAPIController
{

    private Properties complaintProperties;

    private LookupDao lookupDao;

    @RequestMapping(value = "priorities", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_XML_VALUE })
    public @ResponseBody List<String> getComplaintPriorities() throws AcmListObjectsFailedException
    {
        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) getLookupDao().getLookupByName("priorities").getEntries();
        return lookupEntries.stream().map(StandardLookupEntry::getKey).collect(Collectors.toList());
    }

    @RequestMapping(value = "types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<String> getComplaintTypes() throws AcmListObjectsFailedException
    {
        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) getLookupDao().getLookupByName("complaintTypes").getEntries();
        return lookupEntries.stream().map(StandardLookupEntry::getKey).collect(Collectors.toList());
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }
}
