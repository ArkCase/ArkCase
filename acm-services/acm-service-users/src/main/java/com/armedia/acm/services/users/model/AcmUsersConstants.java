package com.armedia.acm.services.users.model;

import com.armedia.acm.objectonverter.DateFormats;

/**
 * Created by manoj.dhungana on 7/21/2015.
 */
public interface AcmUsersConstants
{
    String SOLR_DATE_FORMAT = DateFormats.DEFAULT_DATE_FORMAT;

    /**
     * Date format for date-only fields, where the UI does not send a time component, but only the date.
     */
    String ISO_DATE_FORMAT = "yyyy-MM-dd";

    int USER_ID_MIN_CHAR_LENGTH = 3;

}
