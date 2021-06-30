package com.armedia.acm.plugins.ecm.web.api;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maksud.sharif on 1/15/2016.
 */

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class FolderDocumentCountAPIController
{
    private Logger log = LogManager.getLogger(getClass());
    private ExecuteSolrQuery executeSolrQuery;
    private SearchResults searchResults;

    /**
     * Return a map data structure, where the keys are the names of each top-level folder in the target object,
     * and the values are the count of all documents under each such folder (including subfolders). The root folder
     * has the special name "base". The base folder document count includes all documents stored directly in the
     * root folder of the container (it does not include documents from any subfolders). The document count for each
     * top-level folder includes all documents in that folder, plus all sub-folders of that folder.
     *
     * @param auth
     *            This user must be allowed to read the target object.
     * @param objectType
     *            Object type of the target object, e.g. COMPLAINT, CASE_FILE, TASK. Must be a container object.
     * @param objectId
     *            Object ID of the target object.
     * @return Map of strings (the folder names, with "base" as the special name for the root folder) to
     *         Longs (the count of documents in that folder).
     * @throws AcmListObjectsFailedException
     *             If something went wrong querying Solr for the document counts.
     */
    @PreAuthorize("hasPermission(#objectId, #objectType, 'read|write|group-read|group-write')")
    @RequestMapping(value = "/folder/counts/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Integer> folderDocumentCountList(
            Authentication auth,
            @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId) throws AcmListObjectsFailedException
    {

        Map<String, Integer> documentCounts = new HashMap<>();

        try
        {
            // This query returns the total count of files and folders in the root of the target object. The total
            // file count becomes the value of the "base" key in the returned map. For each folder in the root folder,
            // we go on to get the document count for that folder.
            // Note, we only ask for one document, since for this query, we only care about the facets. We don't
            // need the actual documents.
            String topLevelFilesAndFolders = getExecuteSolrQuery().getResultsByPredefinedQuery(
                    auth,
                    SolrCore.ADVANCED_SEARCH,
                    "{!join from=folder_id_i to=parent_folder_id_i}object_type_s:CONTAINER AND parent_object_id_i:" + objectId
                            + " AND parent_type_s:" + objectType,
                    0,
                    1,
                    "object_id_s ASC",
                    "facet.field=object_type_s&facet=true&fq=object_type_s:FILE OR object_type_s:FOLDER&fq=hidden_b:false&fl=name,object_type_s,object_id_s");

            log.debug("Solr topLevelFilesAndFolders: " + topLevelFilesAndFolders);

            JSONObject topLevelFacets = getSearchResults().getFacetFields(topLevelFilesAndFolders);

            JSONArray objectTypeFacets = topLevelFacets.getJSONArray("object_type_s");

            int baseFiles = getFacetCount(objectTypeFacets, "FILE");

            documentCounts.put("base", baseFiles);

            int baseFolders = getFacetCount(objectTypeFacets, "FOLDER");

            if (baseFolders > 0)
            {
                // This query returns all the top-level folders in the root folder. We can ask for exactly the
                // number of records we need, since we know exactly how many such folders exist. We don't need
                // facets in this query.
                String topLevelFolders = getExecuteSolrQuery().getResultsByPredefinedQuery(
                        auth,
                        SolrCore.ADVANCED_SEARCH,
                        "{!join from=folder_id_i to=parent_folder_id_i}object_type_s:CONTAINER AND parent_object_id_i:" + objectId
                                + " AND parent_type_s:" + objectType,
                        0,
                        baseFolders,
                        "object_id_s ASC",
                        "fq=object_type_s:FOLDER&fq=hidden_b:false&fl=name,object_id_s");

                log.debug("Solr topLevelFolders: " + topLevelFolders);

                JSONArray folders = getSearchResults().getDocuments(topLevelFolders);

                for (int current = 0; current < folders.length(); current++)
                {
                    JSONObject folder = folders.getJSONObject(current);
                    String folderName = folder.getString("name");
                    String folderId = folder.getString("object_id_s");

                    int docCountForFolder = countDocsForFolder(auth, folderId);

                    documentCounts.put(folderName, docCountForFolder);
                }
            }

            return documentCounts;

        }
        catch (SolrException e)
        {
            throw new AcmListObjectsFailedException("files", e.getMessage(), e);
        }

    }

    /**
     * This method recursively calls itself, if the desired folderId includes subfolders. The document counts
     * for each such subfolder are accumulated into a total document count for the desired folderId.
     *
     * @param auth
     *            User who is asking for document counts
     * @param folderId
     *            The folder that we want the document counts for.
     * @return Number of documents in the folderId, plus all subfolders (if any)
     * @throws SolrException
     */
    private int countDocsForFolder(Authentication auth, String folderId) throws SolrException
    {
        int docCount = 0;

        // This query gets the facet counts for files and folders under the desired folderId. We don't care about the
        // actual documents (we only care about the facets), so we only as for one document.
        String folderFacetResults = getExecuteSolrQuery().getResultsByPredefinedQuery(
                auth,
                SolrCore.ADVANCED_SEARCH,
                "parent_folder_id_i:" + folderId,
                0,
                1,
                "object_id_s ASC",
                "facet.field=object_type_s&facet=true&fq=object_type_s:FILE OR object_type_s:FOLDER&fq=hidden_b:false&fl=name,object_type_s,id");

        log.debug("Solr folderFacetResults: " + folderFacetResults);

        JSONObject folderFacets = getSearchResults().getFacetFields(folderFacetResults);
        JSONArray objectTypeFacets = folderFacets.getJSONArray("object_type_s");

        // Add the count of files directly in the folder to the docCount.
        int folderFiles = getFacetCount(objectTypeFacets, "FILE");
        docCount += folderFiles;

        int subFolderCount = getFacetCount(objectTypeFacets, "FOLDER");

        if (subFolderCount > 0)
        {
            // Find the subfolders under this folder, and for each such subfolder, recursively call this same method.
            String subFolderResults = getExecuteSolrQuery().getResultsByPredefinedQuery(
                    auth,
                    SolrCore.ADVANCED_SEARCH,
                    "parent_id_s:" + folderId + " AND parent_type_s:FOLDER",
                    0,
                    subFolderCount,
                    "object_id_s ASC",
                    "fq=object_type_s:FOLDER&fq=hidden_b:false&fl=name,object_type_s,object_id_s");

            log.debug("Solr subFolderResults: " + subFolderResults);

            JSONArray subFolders = getSearchResults().getDocuments(subFolderResults);

            for (int current = 0; current < subFolders.length(); current++)
            {
                JSONObject folder = subFolders.getJSONObject(current);
                String subFolderId = folder.getString("object_id_s");

                int docCountForSubFolder = countDocsForFolder(auth, subFolderId);

                docCount += docCountForSubFolder;
            }
        }

        return docCount;

    }

    /**
     * Solr returns the facets in a JSON array, where the facet names and facet values alternate, like this:
     * ["FILE",5,
     * "FOLDER",4,
     * "ASSOCIATED_TAG",0,
     * "CASE_FILE",0,
     * "COMPLAINT",0,
     * "CONTAINER",0,
     * "COSTSHEET",0,
     * "DISPOSITION",0,
     * "GROUP",0,
     * "NOTIFICATION",0,
     * "PERSON",0,
     * "SUBSCRIPTION",0,
     * "SUBSCRIPTION_EVENT",0,
     * "TASK",0,
     * "TIMESHEET",0,
     * "USER",0].
     * So, to find the count for some particular facet, we look at every other array element and see if that matches
     * the desired facet name. If so, we take the very next array item as the facet value.
     *
     * @param objectTypeFacets
     *            JSON array where strings alternate with ints; acquired from a facet-enabled Solr query.
     * @param objectType
     *            Name of the desired facet
     * @return The facet value associated with objectType; or 0 if there is no such facet name.
     */
    private int getFacetCount(JSONArray objectTypeFacets, String objectType)
    {
        for (int current = 0; current < objectTypeFacets.length(); current += 2)
        {
            if (objectTypeFacets.getString(current).equals(objectType))
            {
                return objectTypeFacets.getInt(current + 1);
            }
        }

        return 0;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }
}
