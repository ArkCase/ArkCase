package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
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

import com.armedia.acm.services.search.model.SearchConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 2/13/15.
 */
public class SearchResults
{
    public int getNumFound(String jsonResults)
    {
        int numFound = 0;

        JSONObject jsonResponseHeader = new JSONObject(jsonResults);
        if (jsonResponseHeader != null && jsonResponseHeader.has(SearchConstants.PROPERTY_RESPONSE))
        {
            JSONObject jsonResponse = jsonResponseHeader.getJSONObject(SearchConstants.PROPERTY_RESPONSE);
            if (jsonResponse != null && jsonResponse.has(SearchConstants.PROPERTY_NUMBER_FOUND))
            {
                numFound = jsonResponse.getInt(SearchConstants.PROPERTY_NUMBER_FOUND);
            }
        }

        return numFound;
    }

    public JSONArray getDocuments(String jsonResults)
    {
        JSONArray retval = null;

        JSONObject jsonResponseHeader = new JSONObject(jsonResults);

        if (jsonResponseHeader.has(SearchConstants.PROPERTY_RESPONSE))
        {
            JSONObject jsonResponse = jsonResponseHeader.getJSONObject(SearchConstants.PROPERTY_RESPONSE);

            if (jsonResponse != null && jsonResponse.has(SearchConstants.PROPERTY_DOCS))
            {
                retval = jsonResponse.getJSONArray(SearchConstants.PROPERTY_DOCS);
            }
        }

        return retval;
    }

    public List<String> getListForField(JSONArray jsonArray, String field)
    {
        List<String> retval = new ArrayList<>();

        if (jsonArray != null && field != null)
        {
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);

                if (object.has(field))
                {
                    retval.add(object.getString(field));
                }
            }
        }

        return retval;
    }

    public String extractString(JSONObject doc, String stringField)
    {
        return doc.has(stringField) ? doc.getString(stringField) : null;
    }

    public Long extractLong(JSONObject doc, String longField)
    {
        String longText = doc.has(longField) ? doc.getString(longField) : null;
        return longText == null ? null : Long.valueOf(longText);
    }

    public Date extractDate(SimpleDateFormat solrFormat, JSONObject doc, String dateField) throws ParseException
    {
        if (doc.has(dateField))
        {
            String dateText = doc.getString(dateField);
            Date date = solrFormat.parse(dateText);
            return date;
        }
        return null;
    }

    public boolean extractBoolean(JSONObject doc, String booleanFieldName)
    {
        boolean booleanField = doc.has(booleanFieldName);
        return booleanField && doc.getBoolean(booleanFieldName);
    }

    public List<String> extractStringList(JSONObject doc, String arrayField)
    {
        if (doc.has(arrayField))
        {
            JSONArray jsonArray = doc.getJSONArray(arrayField);
            if (jsonArray != null)
            {
                List<String> extractedList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    extractedList.add(jsonArray.getString(i));
                }
                return extractedList;
            }
        }
        return null;
    }

    public List<Object> extractObjectList(JSONObject object, String fieldName)
    {
        List<Object> extractedList = new ArrayList<>();

        if (object.has(fieldName))
        {
            JSONArray jsonArray = object.getJSONArray(fieldName);
            if (jsonArray != null)
            {
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    extractedList.add(jsonArray.get(i));
                }
            }
        }

        return extractedList;
    }

    public JSONObject getFacetFields(String jsonResults)
    {
        JSONObject retval = null;

        if (jsonResults != null)
        {
            JSONObject json = new JSONObject(jsonResults);

            if (json != null && json.has(SearchConstants.PROPERTY_FACET_COUNTS))
            {
                JSONObject jsonFacetCounts = json.getJSONObject(SearchConstants.PROPERTY_FACET_COUNTS);

                if (jsonFacetCounts != null && jsonFacetCounts.has(SearchConstants.PROPERTY_FACET_FIELDS))
                {
                    retval = jsonFacetCounts.getJSONObject(SearchConstants.PROPERTY_FACET_FIELDS);
                }
            }
        }

        return retval;
    }
}
