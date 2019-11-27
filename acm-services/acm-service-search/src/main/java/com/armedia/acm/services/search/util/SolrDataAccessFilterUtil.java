package com.armedia.acm.services.search.util;

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

import com.armedia.acm.core.AcmUserAuthorityContext;
import com.armedia.acm.services.search.model.QueryParameter;
import com.armedia.acm.services.search.model.solr.SolrDataAccessOptions;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Java implementation of dataAccessFilter.groovy {@see ../resources/scripts/dataAccessFilter.groovy}
 */
public class SolrDataAccessFilterUtil
{

    /**
     * Process and apply data access filter to {@code solrQuery} with given {@code options} and {@code authentication}
     *
     * @param authentication {@link Authentication} user to apply DAC for
     * @param options        {@link SolrDataAccessOptions} options for how to apply DAC
     * @param solrQuery      {@link SolrQuery} to apply DAC to
     * @return {@link SolrQuery} with DAC applied
     */
    public static SolrQuery process(Authentication authentication, SolrDataAccessOptions options, SolrQuery solrQuery)
    {
        Map<String, String> processed = process(authentication, solrQuery, options);
        return apply(processed, solrQuery);
    }

    /**
     * Process given {@link Authentication} and {@link SolrDataAccessOptions} and generate data access control
     * filters
     *
     * @param authentication {@link Authentication} user to apply DAC for
     * @param options        {@link SolrDataAccessOptions} options for how to apply DAC
     * @return {@code Map<String, String>} with data access control filter properties
     */
    public static Map<String, String> process(Authentication authentication, SolrQuery solrQuery, SolrDataAccessOptions options)
    {
        if (!(authentication instanceof AcmUserAuthorityContext))
        {
            throw new IllegalStateException("Invalid authentication received, unable to apply DAC");
        }

        Map<String, String> apply = new HashMap<>();

        AcmUserAuthorityContext context = (AcmUserAuthorityContext) authentication;
        Long authenticatedUserId = context.getUserIdentity();
        Set<Long> authenticatedUserGroupIds = context.getGroupAuthorities();
        String targetType = getObjectType(solrQuery, options.getQueryParameters()).orElse(null);

        StringBuilder dataAccessFilter = new StringBuilder();
        StringBuilder denyAccessFilter = new StringBuilder();

        if (options.isIncludeDACFilter())
        {
            if (options.isEnableDocumentACL()
                    || targetType == null
                    || (!"FILE".equalsIgnoreCase(targetType)
                    && !"FOLDER".equalsIgnoreCase(targetType)
                    && !"CONTAINER".equalsIgnoreCase(targetType)))
            {
                dataAccessFilter
                        .append("(protected_object_b:true AND (allow_user_ls:")
                        .append(authenticatedUserId);

                // include records where current user is in a group on allow_group_ls
                authenticatedUserGroupIds.forEach(groupId ->
                        dataAccessFilter
                                .append(" OR allow_group_ls:")
                                .append(groupId)
                );

                dataAccessFilter
                        .append(")) OR (public_doc_b:true OR protected_object_b:false OR (*:* -protected_object_b:[* TO *]) OR parent_allow_user_ls:")
                        .append(authenticatedUserId);

                authenticatedUserGroupIds.forEach(groupId ->
                        dataAccessFilter
                                .append(" OR parent_allow_group_ls:")
                                .append(groupId)
                );

                dataAccessFilter.append(")");
            }
        }

        if (options.isIncludeDenyAccessFilter())
        {
            denyAccessFilter
                    .append("-deny_user_ls:")
                    .append(authenticatedUserId);

            authenticatedUserGroupIds.forEach(groupId ->
                    denyAccessFilter
                            .append(" AND -deny_group_ls:")
                            .append(groupId)
            );

            denyAccessFilter
                    .append(" AND -deny_parent_user_ls:")
                    .append(authenticatedUserId);

            authenticatedUserGroupIds.forEach(groupId ->
                    denyAccessFilter
                            .append(" AND -deny_parent_group_ls:")
                            .append(groupId)
            );
        }

        if (options.isFilterSubscriptionEvents())
        {
            String subscribedFilter = "{!join from=id to=related_subscription_ref_s}object_type_s:SUBSCRIPTION";
            apply.put("isSubscribed", subscribedFilter);
        }

        if (dataAccessFilter.length() > 0)
        {
            apply.put("dataAccessFilter", dataAccessFilter.toString());
        }

        if (denyAccessFilter.length() > 0)
        {
            apply.put("denyAccessFilter", denyAccessFilter.toString());
        }

        return apply;
    }

    /**
     * Applies result of {@link SolrDataAccessFilterUtil#process(Authentication, SolrDataAccessOptions, SolrQuery)} to query
     *
     * @param dataAccessProperties result of {@link SolrDataAccessFilterUtil#process(Authentication, SolrDataAccessOptions, SolrQuery)}
     * @param query                {@link SolrQuery} to apply data access result to
     */
    protected static SolrQuery apply(Map<String, String> dataAccessProperties, SolrQuery query)
    {
        if (query == null || dataAccessProperties.isEmpty())
        {
            return query;
        }

        if (dataAccessProperties.containsKey("dataAccessFilter"))
        {
            query.addFilterQuery(dataAccessProperties.get("dataAccessFilter"));
        }

        if (dataAccessProperties.containsKey("denyAccessFilter"))
        {
            query.addFilterQuery(dataAccessProperties.get("denyAccessFilter"));
        }

        if (dataAccessProperties.containsKey("isSubscribed"))
        {
            query.addFilterQuery(dataAccessProperties.get("isSubscribed"));
        }

        return query;
    }

    /**
     * Attempts to find ArkCase objectType from query and/or queryParameters
     *
     * @param solrQuery       {@link SolrQuery} to search through for an objectType
     * @param queryParameters {@code List} of {@link QueryParameter} to search through for an objectType
     * @return {@link Optional} of any found objectType
     */
    private static Optional<String> getObjectType(SolrQuery solrQuery, List<QueryParameter> queryParameters)
    {
        // find object type from rowQueryParameters that looks like "...object_type_s:FILE..."
        Optional<String> query_param_object_type_s = queryParameters.stream()
                .filter(qp -> qp.getValue().contains("object_type_s:"))
                .findFirst()
                .map(QueryParameter::getValue)
                .filter(StringUtils::isNotEmpty)
                .map(getTypeFromSolrObjectType());

        if (query_param_object_type_s.isPresent()) return query_param_object_type_s;

        // find object type from solr query
        Optional<String> query_object_type_s = Stream.of(solrQuery.getQuery())
                .filter(StringUtils::isNotEmpty)
                .filter(query -> query.contains("object_type_s:"))
                .findFirst()
                .map(getTypeFromSolrObjectType());

        if (query_object_type_s.isPresent()) return query_object_type_s;

        // find object type from query "id:103-FILE"
        Optional<String> query_param_id = queryParameters.stream()
                .filter(queryParameter -> queryParameter.getValue().contains("id:"))
                .findFirst()
                .map(QueryParameter::getValue)
                .filter(StringUtils::isNotEmpty)
                .map(getTypeFromSolrId());

        if (query_param_id.isPresent()) return query_param_id;

        return Stream.of(solrQuery.getQuery())
                .filter(StringUtils::isNotEmpty)
                .filter(query -> query.contains("id:"))
                .findFirst()
                .map(getTypeFromSolrId());

    }

    /**
     * Extract object type from Solr 'object_type_s' with the format: /object_type_s:([a-zA-Z0-9_]*)(.*)/;
     *
     * @return {@code String} objectType found from 'id' or null if not found
     */
    private static Function<String, String> getTypeFromSolrObjectType()
    {
        return queryParameterValue ->
        {
            String regex = "object_type_s:([a-zA-Z0-9_]*)(.*)";
            Matcher matcher = Pattern.compile(regex).matcher(queryParameterValue);
            if (matcher.find() && matcher.groupCount() >= 1)
            {
                return matcher.group(1).trim();
            }
            return null;
        };
    }


    /**
     * Extract object type from Solr 'id' with the format: /id:([0-9]+)\-([a-zA-Z0-9_]*)(.*)/;
     *
     * @return {@code String} objectType found from 'id' or null if not found
     */
    private static Function<String, String> getTypeFromSolrId()
    {
        return id -> {
            String regex = "([0-9]+)-([a-zA-Z0-9_]*)(.*)";
            Matcher matcher = Pattern.compile(regex).matcher(id);
            if (matcher.find() && matcher.groupCount() >= 2)
            {
                return matcher.group(2).trim();
            }
            return null;
        };
    }
}
