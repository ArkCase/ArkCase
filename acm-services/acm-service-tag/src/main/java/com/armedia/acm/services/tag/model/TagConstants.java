package com.armedia.acm.services.tag.model;

/**
 * Created by marjan.stefanoski on 31.03.2015.
 */
public interface TagConstants {


    String OBJECT_TYPE="TAG";

    String TAGS = "tags";
    String TAG_NAME ="name";
    String TAG_VALUE = "value";
    String TAG_DESC = "desc";

    int FIRST_ROW = 0;
    int MAX_ROWS = 100;
    String SORT = "";

    String SOLR_RESPONSE_BODY = "response";
    String SOLR_RESPONSE_DOCS = "docs";
    String SOLR_ID = "id";
    String SOLR_ID_SPLITER = "-";

    int ZERO = 0;

    String SOLR_QUERY_GET_ASSOCIATED_TAG_BY_OBJECT_ID_AND_OBJECT_TYPE = "tag.associated.by.object.id.and.type";
    String SOLR_PLACEHOLDER_PARENT_TYPE ="${parentType}";
    String SOLR_PLACEHOLDER_PARENT_ID ="${parentId}";

}
