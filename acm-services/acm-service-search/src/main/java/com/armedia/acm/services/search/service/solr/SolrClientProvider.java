package com.armedia.acm.services.search.service.solr;

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

import com.armedia.acm.services.search.model.solr.SolrClientType;
import com.armedia.acm.services.search.model.solr.SolrConfig;

import org.apache.solr.client.solrj.SolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Creates and configures a {@link SolrClient} from {@link SolrConfig}
 */
public class SolrClientProvider implements InitializingBean
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SolrClientProvider.class);
    private SolrClient client;
    private SolrConfig configuration;

    @Override
    public void afterPropertiesSet()
    {
        init();
    }

    protected void init()
    {
        Assert.notNull(getConfiguration(), "Invalid Solr Configuration, no configuration provided");

        SolrClientType clientType = getConfiguration().getClientType();
        this.client = clientType.build(getConfiguration());
    }

    public SolrClient getClient()
    {
        return client;
    }

    protected SolrConfig getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(SolrConfig configuration)
    {
        this.configuration = configuration;
    }
}
