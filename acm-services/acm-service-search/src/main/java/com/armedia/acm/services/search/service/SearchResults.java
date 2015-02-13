package com.armedia.acm.services.search.service;

import com.armedia.acm.services.search.model.SearchConstants;
import org.json.JSONObject;

/**
 * Created by armdev on 2/13/15.
 */
public class SearchResults
{
    public int getNumFound(String jsonResults)
    {
        JSONObject jsonResponseHeader = new JSONObject(jsonResults);
        JSONObject jsonResponse = jsonResponseHeader.getJSONObject(SearchConstants.PROPERTY_RESPONSE);
        int numFound = jsonResponse.getInt(SearchConstants.PROPERTY_NUMBER_FOUND);

        return numFound;
    }
}
