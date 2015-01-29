package com.armedia.acm.services.subscription.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by marjan.stefanoski on 29.01.2015.
 */
public class AcmJpaSubscribeEventBatchUpdateService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * The default run date to use if this generator has never run before (or if the properties file that stores the
     * last run date is missing)
     */
    private static final String DEFAULT_LAST_RUN_DATE = "1970-01-01T00:00:00Z";

    /**
     * The property key to use in the properties file that stores the last run date.
     */
    private static final String SOLR_LAST_RUN_DATE_PROPERTY_KEY = "solr.last.run.date";

}
