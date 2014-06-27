package com.armedia.acm.services.search.service;

/**
 * Created by armdev on 6/24/14.
 */
public interface SearchConstants
{
    /**
     * The date format SOLR expects.  Any other date format causes SOLR to throw an exception.
     */
    String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    String QUICK_SEARCH_JMS_QUEUE_NAME = "jms://solrQuickSearch.in";

    String SOLR_OBJECT_TYPE_FIELD_NAME = "object_type_s";

}
