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

import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_DEFAULT_FIELD_SUGGEST;
import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_PARAM_DEFAULT_FIELD;
import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_PARAM_FACET;
import static com.armedia.acm.services.search.model.solr.SolrConstants.SOLR_WRITER_JSON;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.NoOpResponseParser;
import org.apache.solr.common.params.CommonParams;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Nov, 2019
 */
public class ArkCaseSolrUtils
{
    private static final Logger LOGGER = LogManager.getLogger(ArkCaseSolrUtils.class);

    /**
     * Configure {@link HttpSolrClient} with given {@link SolrConfig}
     *
     * @return configured {@link HttpSolrClient}
     */
    static Function<SolrConfig, SolrClient> buildHttpClient()
    {
        return configuration -> getHttpClientConfigurer().apply(configuration);
    }

    /**
     * Configure {@link CloudSolrClient} with given {@link SolrConfig}
     *
     * @return configured {@link CloudSolrClient}
     */
    static Function<SolrConfig, SolrClient> cloudClientBuilderFunction()
    {
        return configuration -> getCloudClientConfigurer().apply(configuration);
    }

    /**
     * Configure {@link SolrQuery} with given {@link SolrConfig} for {@link SolrCore#ADVANCED_SEARCH}
     * For SolrJ
     *
     * @return {@link Consumer <>} that will configure SolrQuery
     */
    static BiConsumer<SolrConfig, SolrQuery> advancedSearchQueryConfigurer()
    {
        return (configuration, query) -> getAdvancedSearchQueryConfigurer().accept(query, configuration);
    }

    /**
     * Configure {@link SolrQuery} with given {@link SolrConfig} for {@link SolrCore#ADVANCED_SUGGESTER_SEARCH}
     * For SolrJ
     *
     * @return {@link Consumer<>} that will configure SolrQuery
     */
    static BiConsumer<SolrConfig, SolrQuery> advancedSearchSuggesterQueryConfigurer()
    {
        return (configuration, query) -> getAdvancedSearchSuggesterQueryConfigurer().accept(query, configuration);
    }

    /**
     * Configure {@link SolrQuery} with given {@link SolrConfig} for {@link SolrCore#ADVANCED_SUGGESTER_SEARCH}
     * For SolrJ
     *
     * @return {@link Consumer<>} that will configure SolrQuery
     */
    static BiConsumer<SolrConfig, SolrQuery> notificationSearchQueryConfigurer()
    {
        return (configuration, query) -> getNotificationSearchQueryConfigurer().accept(query, configuration);
    }

    /**
     * Allow Http Client configurer to be delegated to runtime {@link SolrConfig}
     *
     * @return {@link Function<>} that will configure {@link HttpSolrClient}
     */
    public static Function<SolrConfig, SolrClient> getHttpClientConfigurer()
    {
        return configuration -> {
            HttpSolrClient.Builder builder = new HttpSolrClient.Builder()
                    .withBaseSolrUrl(buildSolrURI(configuration))
                    .withConnectionTimeout(configuration.getConnectionTimeout())
                    .withSocketTimeout(configuration.getSocketTimeout());

            return builder.build();
        };
    }

    /**
     * Build URI to access Solr from this {@link SolrConfig}'s properties
     */
    private static String buildSolrURI(SolrConfig config)
    {
        String uri = UriComponentsBuilder.newInstance()
                .scheme(config.getProtocol())
                .host(config.getHost())
                .port(config.getPort())
                .path(config.getContextRoot())
                .toUriString();
        LOGGER.debug("Solr Client URI: [{}]", uri);

        return uri;
    }

    /**
     * Allow Cloud Client configurer to be delegated to runtime {@link SolrConfig}
     *
     * @return {@link Function<>} that will configure {@link CloudSolrClient}
     */
    public static Function<SolrConfig, SolrClient> getCloudClientConfigurer()
    {
        return configuration -> {
            CloudSolrClient.Builder builder = new CloudSolrClient.Builder()
                    .withSolrUrl(buildSolrURI(configuration))
                    .withParallelUpdates(configuration.isParallelUpdates())
                    .withConnectionTimeout(configuration.getConnectionTimeout())
                    .withSocketTimeout(configuration.getSocketTimeout());

            if (StringUtils.isNotEmpty(configuration.getZkHosts()))
            {
                builder.withZkHost(Arrays.asList(configuration.getZkHosts().split(",")));
            }

            return builder.build();
        };
    }

    /**
     * Allow query configurer to be delegated to runtime {@link SolrConfig}
     *
     * @return {@link BiConsumer<>} that will configure SolrQuery
     */
    public static BiConsumer<SolrQuery, SolrConfig> getAdvancedSearchQueryConfigurer()
    {
        return (query, configuration) -> query
                .setRequestHandler("/" + configuration.getSearchHandler())
                .setParam(SOLR_PARAM_FACET, true)
                .setParam(CommonParams.OMIT_HEADER, configuration.isOmitHeader());
    }

    /**
     * Allow query configurer to be delegated to runtime {@link SolrConfig}
     *
     * @return {@link BiConsumer<>} that will configure SolrQuery
     */
    public static BiConsumer<SolrQuery, SolrConfig> getAdvancedSearchSuggesterQueryConfigurer()
    {
        return (query, configuration) -> query
                .setParam(SOLR_PARAM_DEFAULT_FIELD, SOLR_DEFAULT_FIELD_SUGGEST)
                .setRequestHandler("/" + configuration.getSuggestHandler())
                .setParam(CommonParams.OMIT_HEADER, configuration.isOmitHeader());

    }

    /**
     * Allow query configurer to be delegated to runtime {@link SolrConfig}
     *
     * @return {@link BiConsumer<>} that will configure SolrQuery
     */
    public static BiConsumer<SolrQuery, SolrConfig> getNotificationSearchQueryConfigurer()
    {
        return (query, configuration) -> query
                .setRequestHandler("/" + configuration.getSearchHandler())
                .setParam(SOLR_PARAM_FACET, true)
                .setParam(CommonParams.OMIT_HEADER, configuration.isOmitHeader());
    }

    /**
     * Configures {@link SolrRequest} with baseline defaults to use for all requests.
     * Things to possibly configure:
     * 1. Default HTTP method
     * 2. Response parsers
     * 3. Add callbacks
     *
     * @param request
     *            {@link SolrRequest<T>} to configure
     * @param <T>
     *            Any sub-type of {@link SolrRequest}
     * @return {@link SolrRequest} configured with default parameters
     */
    public static <T extends SolrRequest> SolrRequest configureRequest(T request)
    {
        if (request == null)
        {
            LOGGER.error("Invalid Solr Request, unable to configure request.");
            return null;
        }
        // Support RAW json responses instead of the default javabinv2
        NoOpResponseParser rawJsonResponseParser = new NoOpResponseParser();
        rawJsonResponseParser.setWriterType(SOLR_WRITER_JSON);
        request.setResponseParser(rawJsonResponseParser);
        request.setMethod(SolrRequest.METHOD.POST);

        return request;
    }
}
