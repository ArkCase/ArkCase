package com.armedia.acm.services.dataaccess.model;

import com.armedia.acm.objectonverter.DateFormats;

/**
 * Created by armdev on 2/16/15.
 */
public interface DataAccessControlConstants
{
    String ACCESS_MANDATORY = "mandatory";
    String ACCESS_GRANT = "grant";
    String ACCESS_DENY = "deny";
    String ACCESS_LEVEL_PARTICIPANT_TYPE_SEPARATOR = "to";
    String ACCESS_REASON_POLICY = "policy";
    String DEFAULT_ACCESSOR = "*";
    String ACCESS_LEVEL_READ = "read";

    String LAST_RUN_DATE_FORMAT = DateFormats.DEFAULT_DATE_FORMAT;

    String SPACE_REPLACE = "_0020_";
    String COMMA_REPLACE = "_002C_";
    String OPENING_PARENTHESIS_REPLACE = "_0028_";
    String CLOSING_PARENTHESIS_REPLACE = "_0029_";
}
