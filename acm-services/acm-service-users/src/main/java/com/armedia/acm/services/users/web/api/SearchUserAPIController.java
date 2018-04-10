package com.armedia.acm.services.users.web.api;

import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.service.AcmUserService;

import org.json.JSONArray;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = { "/api/v1/users/", "/api/latest/users/" })
public class SearchUserAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmUserService acmUserService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getNUsers(Authentication auth,
            @RequestParam(value = "s", required = false, defaultValue = "name_lcs") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int n) throws MuleException
    {
        return acmUserService.getNUsers(auth, sortBy, sortDirection, startRow, n);
    }

    @RequestMapping(value = "/", params = { "fq" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUsersByName(Authentication auth,
            @RequestParam(value = "fq") String searchFilter,
            @RequestParam(value = "s", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows) throws MuleException
    {
        String solrResponse = acmUserService.getUsersByName(auth, searchFilter, sortBy, sortDirection, startRow, maxRows);
        SearchResults searchResults = new SearchResults();
        JSONArray docs = searchResults.getDocuments(solrResponse);

        return docs.toString();
    }

    public AcmUserService getAcmUserService()
    {
        return acmUserService;
    }

    public void setAcmUserService(AcmUserService acmUserService)
    {
        this.acmUserService = acmUserService;
    }
}
