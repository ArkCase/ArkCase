package com.armedia.acm.services.note.web.api;

import com.armedia.acm.services.note.model.NoteConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
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

@Controller
@RequestMapping({ "/api/v1/plugin/note", "/api/latest/plugin/note" })
public class ListAllNotesAPIController {

    private ExecuteSolrQuery executeSolrQuery;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{parentType}/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findNotesInParentObject(
            @PathVariable("parentType") String parentType,
            @PathVariable("parentId") Long parentId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            @RequestParam(value = "type", required = false, defaultValue = "GENERAL") String type,
            Authentication authentication
    ) throws MuleException, AcmListObjectsFailedException {
        if (log.isInfoEnabled()) {
            log.info("Finding all notes");
        }
        if(type != null && parentId != null && parentType != null) {
            String query = "object_type_s:" + NoteConstants.OBJECT_TYPE + " AND parent_object_id_i:" + parentId + " AND parent_object_type_s:" + parentType + " AND type_s:" + type;
            String sort = "create_date_tdt DESC";

            String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                    query, startRow, maxRows, sort);
            JSONObject resultsObject = new JSONObject(results);

            if (resultsObject.has("response"))
            {
                return resultsObject.getJSONObject("response").toString();
            }
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