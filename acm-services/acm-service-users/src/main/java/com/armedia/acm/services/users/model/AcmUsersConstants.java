package com.armedia.acm.services.users.model;

/**
 * Created by manoj.dhungana on 7/21/2015.
 */
public interface AcmUsersConstants {

    /**
     * The date format SOLR expects.  Any other date format causes SOLR to throw an exception.
     */
    String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";


    /**
     * Date format for date-only fields, where the UI does not send a time component, but only the date.
     */
    String ISO_DATE_FORMAT = "yyyy-MM-dd";

}
