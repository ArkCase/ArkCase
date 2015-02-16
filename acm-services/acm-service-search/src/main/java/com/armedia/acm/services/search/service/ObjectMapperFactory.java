package com.armedia.acm.services.search.service;

import com.armedia.acm.services.search.model.SearchConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by armdev on 6/24/14.
 */
public class ObjectMapperFactory
{
    public ObjectMapper createObjectMapper()
    {
        DateFormat solrDateFormat = new SimpleDateFormat(SearchConstants.SOLR_DATE_FORMAT);

        ObjectMapper mapper = new ObjectMapper();
        mapper = mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper = mapper.setDateFormat(solrDateFormat);

        return mapper;
    }
}
