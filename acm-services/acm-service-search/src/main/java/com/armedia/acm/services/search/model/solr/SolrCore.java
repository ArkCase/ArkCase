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

/*
 * Solr core/collection definition for ArkCase. Includes appropriate query configurers and mapped core/collection names
 * specific to the Solr instance being used.
 */

import org.apache.solr.client.solrj.SolrQuery;

import java.util.function.BiConsumer;
import java.util.function.Function;

public enum SolrCore
{
    ADVANCED_SEARCH(ArkCaseSolrUtils.advancedSearchQueryConfigurer(), SolrConfig::getAdvancedSearchCore),
    ADVANCED_SUGGESTER_SEARCH(ArkCaseSolrUtils.advancedSearchSuggesterQueryConfigurer(), SolrConfig::getAdvancedSearchCore);
    private BiConsumer<SolrConfig, SolrQuery> queryConfigurer;
    private Function<SolrConfig, String> coreNameMapper;

    SolrCore(BiConsumer<SolrConfig, SolrQuery> queryConfigurer, Function<SolrConfig, String> coreNameMapper)
    {
        this.queryConfigurer = queryConfigurer;
        this.coreNameMapper = coreNameMapper;
    }

    /**
     * Modifies given {@link SolrQuery} with {@link SolrConfig} through {@see queryConfigurer}
     *
     * @param configuration {@link SolrConfig} to use to configure {@see query}
     * @param query         {@link SolrQuery} to configure
     * @return this
     */
    public SolrCore configure(SolrConfig configuration, SolrQuery query)
    {
        queryConfigurer.accept(configuration, query);
        return this;
    }

    /**
     * Return collection/core name for this SolrCore using {@link SolrConfig}
     *
     * @param configuration {@link SolrConfig} used to find collection/core name
     * @return {@code String} mapped Solr collection/core name
     */
    public String getCore(SolrConfig configuration)
    {
        return coreNameMapper.apply(configuration);
    }

}
