package com.armedia.acm.services.search.web.api;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class SearchChildrenAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private MuleClient muleClient;

    @RequestMapping(value = "/children", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String children(           
            @RequestParam(value = "parentType", required = true) String parentType,
            @RequestParam(value = "parentId", required = true) Long parentId,
            @RequestParam(value = "childType", required = false, defaultValue = "") String childType,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication
    ) throws MuleException
    {
        String query = "parent_object_type_s:" + parentType + " AND parent_object_id_i:"+ parentId;
        
         if (!"".equals(childType))
        {
         query = query + " AND object_type_s:" + childType;
        }   
        
        
        if ( log.isDebugEnabled() )
        {
            log.debug("User '" + authentication.getName() + "' is searching for '" + query + "'");
        }
     
        Map<String, Object> headers = new HashMap<>();
        headers.put("query", query);
        headers.put("firstRow", startRow);
        headers.put("maxRows", maxRows);
        headers.put("sort", sort);
        headers.put("acmUser", authentication);

        MuleMessage response = getMuleClient().send("vm://quickSearchQuery.in", "", headers);

        log.debug("Response type: " + response.getPayload().getClass());

        if ( response.getPayload() instanceof String )
        {
            return (String) response.getPayload();
        }

        throw new IllegalStateException("Unexpected payload type: " + response.getPayload().getClass().getName());
    }

    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
    }
}
