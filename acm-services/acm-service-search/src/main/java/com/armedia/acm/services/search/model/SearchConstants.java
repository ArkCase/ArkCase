package com.armedia.acm.services.search.model;

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

import com.armedia.acm.objectonverter.DateFormats;

/**
 * Created by armdev on 2/12/15.
 */
public interface SearchConstants
{
    String CATCH_ALL_QUERY = "catch_all:";

    String CORE_QUICK = "QUICK";
    String CORE_ADVANCED = "ADVANCED";

    String DATE_FACET_PRE_KEY = "date.";

    String FACET_FILED = "facet.field=";
    String FACET_QUERY = "facet.query=";

    String SOLR_FILTER_QUERY_ATTRIBUTE_NAME = "&fq=";
    String SOLR_FACET_NAME_CHANGE_COMMAND = "!key=";

    String TIME_PERIOD_DESCRIPTION = "desc";
    String TIME_PERIOD_VALUE = "value";

    String QUOTE_SPLITTER = "\"";
    String DOTS_SPLITTER = ":";
    String PIPE_SPLITTER = "\\|";
    String AND_SPLITTER = "&";

    String PROPERTY_NUMBER_FOUND = "numFound";
    String PROPERTY_RESPONSE = "response";
    String PROPERTY_DOCS = "docs";
    String PROPERTY_NAME = "name";
    String PROPERTY_ASCENDANTS = "ascendants_id_ss";
    String PROPERTY_FACET_COUNTS = "facet_counts";
    String PROPERTY_FACET_FIELDS = "facet_fields";
    String PROPERTY_OBJECT_ID_S = "object_id_s";

    String PROPERTY_FILE_CATEGORY = "category_s";
    String PROPERTY_FILE_TYPE = "type_s";
    String PROPERTY_CREATED = "create_tdt";
    String PROPERTY_MODIFIED = "last_modified_tdt";
    String PROPERTY_CREATOR = "author";
    String PROPERTY_MODIFIER = "modifier_s";
    String PROPERTY_OBJECT_TYPE = "object_type_s";
    String PROPERTY_OBJECT_TYPE_FACET = "object_type_facet";
    String PROPERTY_VERSION = "version_s";
    String PROPERTY_CMIS_VERSION_SERIES_ID = "cmis_version_series_id_s";
    String PROPERTY_MIME_TYPE = "mime_type_s";
    String PROPERTY_EXT = "ext_s";
    String PROPERTY_STATUS = "status_s";
    String PROPERTY_PUBLIC_FLAG = "public_flag_b";
    String PROPERTY_REDACTION_STATUS = "redaction_status_s";
    String PROPERTY_REVIEW_STATUS = "review_status_s";
    String PROPERTY_GROUPS_ID_SS = "groups_id_ss";
    String PROPERTY_QUEUE_ID_S = "queue_id_s";
    String PROPERTY_QUEUE_NAME_S = "queue_name_s";
    String PROPERTY_QUEUE_ORDER = "queue_order_i";
    String PROPERTY_PARENT_OBJECT_TYPE_S = "parent_object_type_s";
    String PROPERTY_PARENT_OBJECT_ID_I = "parent_object_id_i";
    String PROPERTY_PAGE_COUNT_I = "page_count_i";
    String PROPERTY_OBJECT_TYPE_S = "object_type_s";
    String PROPERTY_ASSIGNEE_ID = "assignee_s";
    String PROPERTY_MADE_PUBLIC_DATE = "made_public_date_tdt";

    String USER = "$[user]";
    /**
     * The date format SOLR expects. Any other date format causes SOLR to throw an exception.
     */
    String SOLR_DATE_FORMAT = DateFormats.DEFAULT_DATE_FORMAT;

    /**
     * Date format for date-only fields, where the UI does not send a time component, but only the date.
     */
    String ISO_DATE_FORMAT = "yyyy-MM-dd";

    String SORT_DESC = "DESC";

    /**
     * Query operators
     */
    String OPERATOR_AND = "AND";
    String OPERATOR_OR = "OR";

    /**
     * Key in the properties file that hold name of the Solr properties for search objects by query
     */
    String SEARCH_QUERY_PROPERTIES_KEY = "search.tree.searchQuery";

    String TIME_ZONE_UTC = "UTC";

    String EVENT_TYPE = "eventType.";

    String FACETED_SEARCH_ENCODING = "UTF-8";

    String EXPORT_FIELDS = "facet.search.export.fields";

    String SEPARATOR_COMMA = ",";

    Integer MAX_RESULT_ROWS = 100000;
    String DEFAULT_FIELD = "catch_all";

    String PROPERTY_LINK = "link_b";

    String PROPERTY_DUPLICATE = "duplicate_b";

    String PROPERTY_FILE_CUSTODIAN = "custodian_s";

    String PDFEXPORT_STYLESHEET = "searchexportpdf-document.xsl";

    String SEARCH_RESULTS_PDF_EXPORT = "searchresultspdf";

}
