package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.SolrSearchService;
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

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequestMapping( { "/api/v1/plugin/search", "/api/latest/plugin/search"} )
public class PersonSearchByNameAndContactMethodAPIController
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private SolrSearchService solrSearchService;

    @RequestMapping(value = "/personSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String person(
            @RequestParam(value = "name", required = true) String personName,
            @RequestParam(value = "cm", required = true) String contactMethod,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication,
            HttpServletResponse httpResponse
    ) throws MuleException, UnsupportedEncodingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("User '" + authentication.getName() + "' is searching for name '" + personName + "' "
                + ", contact method value: '" + contactMethod + "'");
        }

        // use a SOLR join query to restrict person results to persons with a contact method matching the
        // given value.
        final String encodedContactMethodJoin = URLEncoder.encode("{!join from=id to=contact_method_ss}", "UTF-8");

        String query = "object_type_s:PERSON AND name:" + URLEncoder.encode(personName, "UTF-8") + " AND " +
                encodedContactMethodJoin + "value_parseable:" + URLEncoder.encode(contactMethod, "UTF-8");
        String sort = "last_name_lcs ASC, first_name_lcs ASC";

        query = query.replaceAll(" ", "+");
        sort = sort.replaceAll(" ", "+");

        String results = getSolrSearchService().search(authentication, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort);

        httpResponse.addHeader("X-JSON", results);

        return results;

    }

    public SolrSearchService getSolrSearchService()
    {
        return solrSearchService;
    }

    public void setSolrSearchService(SolrSearchService solrSearchService)
    {
        this.solrSearchService = solrSearchService;
    }
}
