package com.armedia.acm.services.config.lookups.web.api;

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
        "/api/v1/service/config/annotationtags",
        "/api/latest/service/config/annotationtags" })
public class GetAnnotationTagsAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private LookupDao lookupDao;

    @RequestMapping(value = "/annotationTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String[] annotationsTags(Authentication authentication, HttpSession session) throws AcmListObjectsFailedException
    {

        log.debug("Finding annotation types");

        List<StandardLookupEntry> lookupEntries = (List<StandardLookupEntry>) getLookupDao().getLookupByName("annotationTags").getEntries();
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
