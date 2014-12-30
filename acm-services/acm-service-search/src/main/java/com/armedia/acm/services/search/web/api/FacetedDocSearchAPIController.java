package com.armedia.acm.services.search.web.api;

import org.mule.api.MuleException;
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

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by marjan.stefanoski on 18.12.2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class FacetedDocSearchAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());

    private MuleClient muleClient;
    @RequestMapping(value = "/facetedDocumentSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String facetedDocumentSearch (
            @RequestParam(value = "createDate", required = false) Date createDate,
            @RequestParam(value = "modifyDate", required = false) Date modifyDate,
            @RequestParam(value = "creator", required = false) String creator,
            @RequestParam(value = "modifier", required = false) String modifier,
            @RequestParam(value = "parentType", required = false) String parentType,
            @RequestParam(value = "fileStatus", required = false) String fileStatus,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication,
            HttpServletResponse httpResponse
    ) throws MuleException, Exception {

        return null;
    }

    public MuleClient getMuleClient() {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient) {
        this.muleClient = muleClient;
    }
}
