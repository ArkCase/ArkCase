package com.armedia.acm.services.suggestion.service.impl;

/*-
 * #%L
 * acm-service-case-suggestion
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
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.suggestion.model.SuggestedCase;
import com.armedia.acm.services.suggestion.service.SimilarCasesService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SimilarCasesServiceImpl implements SimilarCasesService
{

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private ExecuteSolrQuery executeSolrQuery;

    @Override
    public List<SuggestedCase> findSimilarCases(String title, Boolean isPortal, Long objectId, Authentication auth)
            throws ParseException, SolrException
    {

        List<SuggestedCase> similarCases = new ArrayList<>();

        if (isPortal)
        {
            similarCases.addAll(findSolrCasesByFileContent(title, isPortal, objectId, auth));
            return similarCases;
        }
        else
        {
            similarCases.addAll(findSolrCasesByTitle(title, isPortal, objectId, auth));
            similarCases.addAll(findSolrCasesByFileContent(title, isPortal, objectId, auth));
            return filterCaseRecordDuplicates(similarCases);
        }

    }

    private List<SuggestedCase> findSolrCasesByTitle(String title, Boolean isPortal, Long objectId, Authentication auth)
            throws ParseException, SolrException
    {
        List<SuggestedCase> records = new ArrayList<>();

        if (title != null && !title.isEmpty())
        {
            List<String> wordArray = new ArrayList<>();
            wordArray.add(title.trim());
            if (title.trim().contains(" "))
            {
                wordArray.addAll(Arrays.asList(title.trim().split(" ")));
            }

            for (String word : wordArray)
            {
                if (word.length() >= 3)
                {

                    log.debug(String.format("Finding similar cases by title to [%s]", word));

                    String query;
                    if (isPortal)
                    {
                        query = String.format("object_type_s:CASE_FILE AND request_status_lcs:Released AND title_parseable:*%s*", word);
                    }
                    else
                    {
                        query = String.format("object_type_s:CASE_FILE AND title_parseable:*%s*", word);
                    }

                    String results = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, 99999, "",
                            true, "",
                            false, false, "catch_all");

                    SearchResults searchResults = new SearchResults();
                    JSONArray docFiles = searchResults.getDocuments(results);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                    for (int i = 0; i < docFiles.length(); i++)
                    {
                        JSONObject docFile = docFiles.getJSONObject(i);

                        if (objectId != null && Long.valueOf(docFile.getString("object_id_s")).equals(objectId))
                        {
                            continue;
                        }

                        SuggestedCase suggestedCase = new SuggestedCase();

                        suggestedCase.setCaseId(Long.valueOf(docFile.getString("object_id_s")));
                        suggestedCase.setCaseNumber(docFile.getString("name"));
                        suggestedCase.setCaseTitle(docFile.getString("title_parseable"));
                        suggestedCase.setModifiedDate(formatter.parse(docFile.getString("modified_date_tdt")));
                        suggestedCase.setCaseStatus(docFile.getString("status_lcs"));
                        suggestedCase.setCaseDescription("");
                        suggestedCase.setObjectType(docFile.getString("object_type_s"));

                        if (!docFile.isNull("description_no_html_tags_parseable"))
                        {
                            suggestedCase.setCaseDescription(docFile.getString("description_no_html_tags_parseable"));
                        }

                        records.add(suggestedCase);
                    }
                }
            }
        }

        return records;
    }

    private List<SuggestedCase> findSolrCasesByFileContent(String title, Boolean isPortal, Long objectId, Authentication auth)
            throws ParseException, SolrException
    {
        List<SuggestedCase> records = new ArrayList<>();

        String fileQuery;

        List<String> wordArray = new ArrayList<>();
        wordArray.add(title.trim());
        if (title.trim().contains(" "))
        {
            wordArray.addAll(Arrays.asList(title.trim().split(" ")));
        }

        for (String word : wordArray)
        {
            if (word.length() >= 3)
            {
                List<SuggestedCase> suggestedCaseList = new ArrayList<>();
                if (isPortal)
                {
                    fileQuery = String.format("\"%s\" AND object_type_s:FILE AND parent_ref_s:*CASE_FILE AND public_flag_b:true", word);
                }
                else
                {
                    fileQuery = String.format("\"%s\" AND object_type_s:FILE AND parent_ref_s:*CASE_FILE", word);
                }

                log.debug(String.format("Finding similar cases in content to [%s]", word));

                String fileResults = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, fileQuery, 0, 99999,
                        "",
                        true, "",
                        false, false, "catch_all");

                SearchResults fileSearchResults = new SearchResults();
                JSONArray fileDocFiles = fileSearchResults.getDocuments(fileResults);

                for (int i = 0; i < fileDocFiles.length(); i++)
                {
                    SuggestedCase suggestedCase = new SuggestedCase();
                    SuggestedCase.File file = new SuggestedCase.File();

                    JSONObject docFile = fileDocFiles.getJSONObject(i);

                    file.setFileId(docFile.getString("object_id_s"));
                    file.setFileName(docFile.getString("title_parseable") + docFile.getString("ext_s"));

                    suggestedCase.setCaseNumber(docFile.getString("parent_number_lcs"));
                    suggestedCase.setFile(file);

                    suggestedCaseList.add(suggestedCase);
                }

                for (SuggestedCase sc : suggestedCaseList)
                {
                    String caseQuery;

                    if (!sc.getCaseNumber().isEmpty())
                    {

                        if (isPortal)
                        {
                            caseQuery = String.format("object_type_s:CASE_FILE AND request_status_lcs:Released AND name:%s",
                                    sc.getCaseNumber());
                        }
                        else
                        {
                            caseQuery = String.format("object_type_s:CASE_FILE AND name:%s", sc.getCaseNumber());
                        }

                        String caseResults = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, caseQuery, 0,
                                99999, "",
                                true, "",
                                false, false, "catch_all");

                        SearchResults caseSearchResults = new SearchResults();
                        JSONArray caseDocFiles = caseSearchResults.getDocuments(caseResults);

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                        for (int i = 0; i < caseDocFiles.length(); i++)
                        {
                            JSONObject caseDocFile = caseDocFiles.getJSONObject(i);

                            if (objectId != null && Long.valueOf(caseDocFile.getString("object_id_s")).equals(objectId))
                            {
                                continue;
                            }

                            sc.setCaseId(Long.valueOf(caseDocFile.getString("object_id_s")));
                            sc.setCaseTitle(caseDocFile.getString("title_parseable"));
                            sc.setModifiedDate(formatter.parse(caseDocFile.getString("modified_date_tdt")));
                            sc.setCaseStatus(caseDocFile.getString("status_lcs"));
                            sc.setCaseDescription("");
                            sc.setObjectType(caseDocFile.getString("object_type_s"));

                            if (!caseDocFile.isNull("description_no_html_tags_parseable"))
                            {
                                sc.setCaseDescription(caseDocFile.getString("description_no_html_tags_parseable"));
                            }
                        }
                    }
                }
                records.addAll(suggestedCaseList);
            }
        }

        return records.stream().filter(suggestedCase -> Objects.nonNull(suggestedCase.getCaseId())).collect(Collectors.toList());
    }

    private List<SuggestedCase> filterCaseRecordDuplicates(List<SuggestedCase> list)
    {
        Map<Long, SuggestedCase> filter = new HashMap<>();

        list.forEach(suggestedCase -> filter.putIfAbsent(suggestedCase.caseId, suggestedCase));

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
