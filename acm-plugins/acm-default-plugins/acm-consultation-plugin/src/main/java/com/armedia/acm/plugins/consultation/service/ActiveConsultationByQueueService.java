package com.armedia.acm.plugins.consultation.service;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.services.search.service.SearchResults;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public interface ActiveConsultationByQueueService
{
    String getSolrQueuesResponse(Authentication authentication, int start, int n);

    String getSolrFacetResponse(Authentication authentication);

    Map<String, Long> getNumberOfActiveConsultationsByQueue(List<Object> queuesValues, List<Object> facetValues);

    List<Object> getQueues(Authentication authentication, SearchResults searchResults);

    List<Object> getFacet(Authentication authentication, SearchResults searchResults);

    Long findValue(String queueName, List<Object> facetFieldValue);
}
