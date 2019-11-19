package com.armedia.acm.services.search.model.solr;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

/**
 * Constants for Solr
 */
public interface SolrConstants
{
    String SOLR_WRITER_JSON = "json";
    String SOLR_DEFAULT_FIELD_SUGGEST = "suggest_ngram";

    String SOLR_PARAM_RESPONSE = "response";
    String SOLR_PARAM_INDENT = "indent";
    String SOLR_PARAM_WRITER = "wt";
    String SOLR_PARAM_DEFAULT_FIELD = "df";
    String SOLR_PARAM_FACET = "facet";

    String SOLR_RAW_QUERY_PARAM_SPLITTER = "&";
    String SOLR_RAW_QUERY_SPLITTER = "=";
    String SOLR_SORT_CLAUSE_SPLITTER = " ";

}
