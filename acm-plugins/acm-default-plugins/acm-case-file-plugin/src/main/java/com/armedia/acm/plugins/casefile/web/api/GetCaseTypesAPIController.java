package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.config.lookups.model.StandardLookupEntry;
import com.armedia.acm.services.config.lookups.service.LookupDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping({
        "/api/v1/plugin/casefile",
        "/api/latest/plugin/casefile" })
public class GetCaseTypesAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private LookupDao lookupDao;

    @RequestMapping(value = "/caseTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String[] caseTypes(Authentication authentication, HttpSession session) throws AcmListObjectsFailedException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Finding case types");
        }

        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) getLookupDao().getLookupByName("caseFileTypes").getEntries();
        return lookupEntries.stream().map(StandardLookupEntry::getKey).toArray(String[]::new);
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
