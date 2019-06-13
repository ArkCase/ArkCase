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

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.suggestion.model.SuggestedCase;
import com.armedia.acm.services.suggestion.service.SimilarCasesService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SimilarCasesServiceImpl implements SimilarCasesService
{

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private ExecuteSolrQuery executeSolrQuery;

    @Override
    public List<SuggestedCase> findSimilarCases(String title, Boolean isExtension, Authentication auth) throws MuleException, ParseException {

        List<SuggestedCase> similarCases = new ArrayList<>();

        similarCases.addAll(findSolrCasesByTitle(title, isExtension, auth));
        similarCases.addAll(findSolrCasesByFileContent(title, isExtension, auth));

        if(isExtension)
        {
            return similarCases;
        }
        else
        {
            return filterCaseRecordDuplicates(similarCases);
        }
    }

    private List<SuggestedCase> findSolrCasesByTitle(String title, Boolean isExtension, Authentication auth) throws MuleException, ParseException
    {
        List<SuggestedCase> records = new ArrayList<>();

        log.debug(String.format("Finding similar cases by title to [%s]", title));

        String query;
        if(isExtension)
        {
            query = String.format("object_type_s:CASE_FILE AND request_status_lcs:Released AND title_parseable:*%s*", title);
        }
        else
        {
            query = String.format("object_type_s:CASE_FILE AND title_parseable:*%s*", title);
        }

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, 99999, "", true, "",
                false, false, "catch_all");

        SearchResults searchResults = new SearchResults();
        JSONArray docFiles = searchResults.getDocuments(results);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        for (int i = 0; i < docFiles.length(); i++)
        {
            JSONObject docFile = docFiles.getJSONObject(i);

            SuggestedCase suggestedCase = new SuggestedCase();

            suggestedCase.setCaseId(Long.valueOf(docFile.getString("object_id_s")));
            suggestedCase.setCaseNumber(docFile.getString("name"));
            suggestedCase.setCaseTitle(docFile.getString("title_parseable"));
            suggestedCase.setModifiedDate(formatter.parse(docFile.getString("modified_date_tdt")));
            suggestedCase.setCaseStatus(docFile.getString("status_lcs"));
            suggestedCase.setCaseDescription("");

            if (!docFile.isNull("description_no_html_tags_parseable"))
            {
                suggestedCase.setCaseDescription(docFile.getString("description_no_html_tags_parseable"));
            }

            records.add(suggestedCase);
        }

        return records;
    }

    private List<SuggestedCase> findSolrCasesByFileContent(String title, Boolean isExtension, Authentication auth) throws MuleException, ParseException
    {
        List<SuggestedCase> records = new ArrayList<>();

        log.debug(String.format("Finding similar cases in content to [%s]", title));

        String fileQuery;
        if(isExtension)
        {
            fileQuery = String.format("*%s* AND object_type_s:FILE AND parent_ref_s:*CASE_FILE AND public_flag_b:true", title);
        }
        else
        {
            fileQuery = String.format("*%s* AND object_type_s:FILE AND parent_ref_s:*CASE_FILE", title);
        }

        String fileResults = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, fileQuery, 0, 99999, "", true, "",
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

            records.add(suggestedCase);
        }

        for(SuggestedCase sc : records)
        {
            String caseQuery;

            if(isExtension)
            {
                caseQuery = String.format("object_type_s:CASE_FILE AND request_status_lcs:Released AND name:*%s*", sc.getCaseNumber());
            }
            else
            {
                caseQuery = String.format("object_type_s:CASE_FILE AND name:*%s*", sc.getCaseNumber());
            }

            String caseResults = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, caseQuery, 0, 99999, "", true, "",
                    false, false, "catch_all");

            SearchResults caseSearchResults = new SearchResults();
            JSONArray caseDocFiles = caseSearchResults.getDocuments(caseResults);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            for (int i = 0; i < caseDocFiles.length(); i++)
            {
                JSONObject caseDocFile = caseDocFiles.getJSONObject(i);

                sc.setCaseId(Long.valueOf(caseDocFile.getString("object_id_s")));
                sc.setCaseTitle(caseDocFile.getString("title_parseable"));
                sc.setModifiedDate(formatter.parse(caseDocFile.getString("modified_date_tdt")));
                sc.setCaseStatus(caseDocFile.getString("status_lcs"));
                sc.setCaseDescription("");

                if (!caseDocFile.isNull("description_no_html_tags_parseable"))
                {
                    sc.setCaseDescription(caseDocFile.getString("description_no_html_tags_parseable"));
                }
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
