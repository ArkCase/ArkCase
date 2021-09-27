package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * @author aleksandar.bujaroski
 */
public class ChildDocumentsSearchServiceImpl implements ChildDocumentsSearchService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    @Override
    public String searchChildren(String parentType, Long parentId, String childType, boolean activeOnly,
            boolean exceptDeletedOnly, List<String> extra, String sort, int startRow, int maxRows, Authentication authentication)
            throws SolrException
    {
        String query = "object_type_s:" + childType;
        query = query.concat("&fq=+parent_object_type_s:"+parentType+" +parent_object_id_i:"+parentId);

        if (activeOnly)
        {
            query = query.concat(" -status_lcs:COMPLETE -status_lcs:DELETE -status_lcs:CLOSED -status_lcs:CLOSE");
        }
        if (exceptDeletedOnly)
        {
            if (!activeOnly)
            {
                query = query.concat(" -status_lcs:DELETE");
            }
        }
        
        
        if (extra != null && extra.size() > 0)
        {
            for (String extraParam : extra)
            {
                query += " AND " + extraParam;
            }
        }

        log.debug("User [{}] is searching by query [{}]", authentication.getName(), query);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query,
                startRow, maxRows, sort);
    }

    @Override
    public String searchForChildrenAndGrandchildrenTasks(String parentType, Long parentId, List<String> childTypes, String sort,
            int startRow,
            int maxRows,
            Authentication authentication) throws SolrException
    {

        String rowQueryParameters = String.format(
                "q1=parent_object_type_s:%1$s AND object_type_s:%2$s AND parent_object_id_s:%3$s" +
                        "&q2=({!join from=timesheet_id_i to=object_id_i}parent_object_id_s:%3$s) AND object_type_s:%4$s" +
                        "&fq=object_type_s:TASK&fq=-status_s:DELETE",
                parentType,
                childTypes.get(1),
                parentId.toString(),
                childTypes.get(0));
        String query = String.format(
                "q=({!join from=id to=parent_ref_s v=$q1}) OR (_query_:\"parent_object_type_s:%s AND parent_object_id_i:%d\")" +
                        "OR (({!join from=object_id_i to=parent_object_id_i v=$q2}) AND parent_object_type_s:%s)" +
                        "(({!join from=object_id_i to=parent_object_id_i v=$q2}) AND parent_object_type_s:%<s)",
                parentType,
                parentId,
                childTypes.get(0));

        return getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, query, startRow, maxRows, sort,
                rowQueryParameters);
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
