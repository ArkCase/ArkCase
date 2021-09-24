package com.armedia.acm.services.suggestion.service.impl;

/*-
 * #%L
 * acm-service-object-suggestion
 * %%
 * Copyright (C) 2014 - 2019 ArkObject LLC
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
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.suggestion.model.SuggestedObject;
import com.armedia.acm.services.suggestion.service.SimilarObjectsService;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimilarObjectsServiceImpl implements SimilarObjectsService
{

    private static final int MAX_SIMILAR_OBJECTS = 100;

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private ExecuteSolrQuery executeSolrQuery;

    @Override
    public List<SuggestedObject> findSimilarObjects(String title, String objectType, Boolean isPortal, Long objectId, Authentication auth)
            throws ParseException, SolrException
    {
        List<SuggestedObject> similarObjects = new ArrayList<>();

        if (isPortal)
        {
            similarObjects.addAll(findSolrObjectsByFileContent(title, objectType, isPortal, objectId, auth));
            return similarObjects;
        }
        else
        {
            similarObjects.addAll(findSolrObjectsByTitle(title, objectType, isPortal, objectId, auth));
            similarObjects.addAll(findSolrObjectsByFileContent(title, objectType, isPortal, objectId, auth));
            return filterObjectRecordDuplicates(similarObjects);
        }
    }

    private List<SuggestedObject> findSolrObjectsByTitle(String title, String objectType, Boolean isPortal, Long objectId,
            Authentication auth)
            throws ParseException, SolrException
    {
        List<SuggestedObject> records = new ArrayList<>();

        if (StringUtils.isNotBlank(title))
        {
            log.debug(String.format("Finding similar objects by title to [%s], of type [%s]", title, objectType));

            StringBuilder query = titleToWordsQuery(title, false);

            query.append(" AND object_type_s:").append(objectType);
            if (isPortal && "CASE_FILE".equals(objectType))
            {
                query.append(" AND queue_name_s:").append("Release");
            }
            if (objectId != null)
            {
                query.append(" AND -object_id_s:").append(objectId);
            }

            String results = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query.toString(),
                    0, MAX_SIMILAR_OBJECTS, "", true, "", false, false, "catch_all");

            SearchResults searchResults = new SearchResults();
            JSONArray docFiles = searchResults.getDocuments(results);

            for (int i = 0; i < docFiles.length(); i++)
            {
                JSONObject docFile = docFiles.getJSONObject(i);

                SuggestedObject suggestedObject = populateSuggestedObject(docFile);

                records.add(suggestedObject);
            }
        }

        return records;
    }

    private List<SuggestedObject> findSolrObjectsByFileContent(String title, String objectType, Boolean isPortal, Long objectId,
            Authentication auth)
            throws ParseException, SolrException
    {
        log.debug(String.format("Finding similar objects in content to [%s], of type [%s]", title, objectType));

        List<SuggestedObject> suggestedObjectList = new ArrayList<>();

        if (StringUtils.isNotBlank(title))
        {
            StringBuilder fileQuery = titleToWordsQuery(title, true);

            fileQuery.append(" AND object_type_s:FILE");
            fileQuery.append(" AND parent_ref_s:*").append(objectType);
            fileQuery.append(" AND parent_number_lcs:*");
            if (isPortal)
            {
                fileQuery.append(" AND public_flag_b:true");
            }

            String fileResults = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, fileQuery.toString(),
                    0, MAX_SIMILAR_OBJECTS, "", true, "", false, false, "catch_all");

            SearchResults fileSearchResults = new SearchResults();
            JSONArray fileDocFiles = fileSearchResults.getDocuments(fileResults);

            for (int i = 0; i < fileDocFiles.length(); i++)
            {
                JSONObject docFile = fileDocFiles.getJSONObject(i);

                String objectQuery = String.format("object_type_s:%s AND name:\"%s\"", objectType, docFile.getString("parent_number_lcs"));
                if (isPortal && "CASE_FILE".equals(objectType))
                {
                    objectQuery = objectQuery.concat(" AND queue_name_s:Release");
                }

                String objectResults = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, objectQuery,
                        0, 1, "", true, "", false, false);

                SearchResults objectSearchResults = new SearchResults();
                JSONArray objectDocFiles = objectSearchResults.getDocuments(objectResults);

                if (objectDocFiles.length() < 1)
                {
                    continue;
                }

                JSONObject objectDocFile = objectDocFiles.getJSONObject(0);

                if (objectId != null && Long.valueOf(objectDocFile.getString("object_id_s")).equals(objectId))
                {
                    continue;
                }

                SuggestedObject suggestedObject = populateSuggestedObject(objectDocFile);
                SuggestedObject.File file = populateSuggestedObjectFile(docFile);
                suggestedObject.setFile(file);

                suggestedObjectList.add(suggestedObject);
            }
        }

        return suggestedObjectList.stream().filter(suggestedObject -> Objects.nonNull(suggestedObject.getId()))
                .collect(Collectors.toList());
    }

    private StringBuilder titleToWordsQuery(String title, boolean isContent)
    {

        StringBuilder words = new StringBuilder(Stream.of(title.trim().split(" "))
                .filter(isContent ? word -> word.length() > 2 : word -> word.length() > 0)
                .map(this::encodeWord)
                .map(word -> "\"" + word + "\"")
                .collect(Collectors.joining(" OR ")));

        if (isContent)
        {
            if (words.toString().isEmpty())
            {
                words.append("\"" + encodeWord(title) + "\"");
            }
            else
            {
                words.append(" OR \"" + encodeWord(title) + "\"");
            }
        }
        words.insert(0, "(").append(")");
        return words;
    }

    private String encodeWord(String word)
    {
        try
        {
            return URLEncoder.encode(word, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            log.warn("Error encoding word [{}]", word);
        }
        return word;
    }

    private SuggestedObject.File populateSuggestedObjectFile(JSONObject docFile)
    {
        SuggestedObject.File file = new SuggestedObject.File();

        file.setFileId(docFile.getString("object_id_s"));
        file.setFileName(docFile.getString("title_parseable") + docFile.getString("ext_s"));
        if (docFile.has("made_public_date_tdt"))
        {
            LocalDateTime date = LocalDateTime.parse(docFile.getString("made_public_date_tdt"), DateTimeFormatter.ISO_DATE_TIME);
            file.setMadePublicDate(date);
        }

        return file;
    }

    private SuggestedObject populateSuggestedObject(JSONObject objectDocFile) throws ParseException
    {
        SuggestedObject suggestedObject = new SuggestedObject();

        suggestedObject.setId(Long.valueOf(objectDocFile.getString("object_id_s")));
        suggestedObject.setName(objectDocFile.getString("name"));
        suggestedObject.setTitle(objectDocFile.getString("title_parseable"));
        suggestedObject.setModifiedDate(objectDocFile.getString("modified_date_tdt"));
        suggestedObject.setStatus(objectDocFile.getString("status_lcs"));
        suggestedObject.setDescription("");
        suggestedObject.setType(objectDocFile.getString("object_type_s"));

        if (!objectDocFile.isNull("description_no_html_tags_parseable"))
        {
            suggestedObject.setDescription(objectDocFile.getString("description_no_html_tags_parseable"));
        }
        return suggestedObject;
    }

    private List<SuggestedObject> filterObjectRecordDuplicates(List<SuggestedObject> list)
    {
        Map<Long, SuggestedObject> filter = new HashMap<>();

        list.forEach(suggestedObject -> filter.putIfAbsent(suggestedObject.id, suggestedObject));

        return new ArrayList<>(filter.values());
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
