package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import java.util.List;
import javax.persistence.PersistenceException;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping({ "/api/v1/plugin/person", "/api/latest/plugin/person" })
public class ListPersonAPIController
{
    private ExecuteSolrQuery executeSolrQuery;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/list/{parentType}/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
                public String findPersonBYAssociation(
                    @PathVariable("parentType") String parentType,
                    @PathVariable("parentId") Long parentId,
                    @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
                    @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
                    Authentication authentication
                ) throws MuleException, AcmListObjectsFailedException
                {
                    if ( log.isInfoEnabled() )
                    {
                        log.info("Finding person by parent id '" + parentId + "'" + "parent type '" + parentType+ "'" );
                    }

                    if ( (parentType != null ) && ( parentId != null ) )
                    {

                String query = "object_type_s:PERSON-ASSOCIATION AND parent_id_s:" + parentId + " AND parent_type_s:" + parentType;
                String sort = "create_date_tdt DESC";

                String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                        query, startRow, maxRows, sort);
                JSONObject resultsObject = new JSONObject(results);

                if (resultsObject.has("response"))
                {
                    return resultsObject.getJSONObject("response").toString();
                }

                        return null;
            }
                    throw new AcmListObjectsFailedException("wrong input", "patenType or parentId are: ", null);
                }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
  
}
