package com.armedia.acm.services.participants.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.participants.model.ParticipantConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by marjan.stefanoski on 01.04.2015.
 */

@Controller
@RequestMapping({"/api/v1/service/participant","/api/latest/service/participant"})
public class ListAllParticipantsByObjectTypeAndObjectId {

    private ExecuteSolrQuery executeSolrQuery;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String listParticipants(
            @PathVariable(value = "objectType") String objectType,
            @PathVariable(value = "objectId") Long objectId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication
    ) throws MuleException, AcmListObjectsFailedException {
        if (log.isInfoEnabled()){
            log.info("List all participants on object ['" + objectType + "]:[" + objectId + "]");
        }

        String query = "object_type_s:" + ParticipantConstants.OBJECT_TYPE + " AND parent_object_id_i:" + objectId + " AND parent_object_type_s:" + objectType;
        String sort = "create_date_tdt DESC";

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                query, startRow, maxRows, sort);

        JSONObject resultsObject = new JSONObject(results);

        if (resultsObject.has("response"))
        {
            return resultsObject.getJSONObject("response").toString();
        }

        throw new AcmListObjectsFailedException("wrong input", "user: ", null);
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

