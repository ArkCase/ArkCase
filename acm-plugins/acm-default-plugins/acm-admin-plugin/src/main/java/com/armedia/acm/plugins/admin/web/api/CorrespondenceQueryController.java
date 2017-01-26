package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.QueryType;
import com.armedia.acm.correspondence.service.CorrespondenceService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 25, 2017
 *
 */
@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class CorrespondenceQueryController
{

    private CorrespondenceService correspondenceService;

    @RequestMapping(value = "/queries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceQueryResponce> getAllQueries()
    {
        return generateResponse(correspondenceService.getAllQueries());
    }

    @RequestMapping(value = "/queries/{queryType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceQueryResponce> getQueriesByType(@PathVariable(value = "queryType") QueryType queryType)
    {
        return generateResponse(correspondenceService.getQueriesByType(queryType));
    }

    /**
     * @param queries
     * @return
     */
    private List<CorrespondenceQueryResponce> generateResponse(Map<String, CorrespondenceQuery> queries)
    {
        return queries.entrySet().stream().map(entry -> new CorrespondenceQueryResponce(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * @param correspondenceService the correspondenceService to set
     */
    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }

}
