package com.armedia.acm.plugins.casefile.web.api;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.plugins.casefile.service.ActiveCaseFileByQueueService;
import com.armedia.acm.services.search.service.SearchResults;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by riste.tutureski on 9/22/2015.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/casefile/number/by/queue", "/api/latest/plugin/casefile/number/by/queue" })
public class GetNumberOfActiveCaseFilesByQueueAPIController
{
    private Logger LOG = LogManager.getLogger(getClass());

    private ActiveCaseFileByQueueService activeCaseFileByQueueService;

    /**
     * REST api for retrieving active case files by queue
     *
     * @param authentication
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public Map<String, Long> getNumberOfActiveCaseFilesByQueue(Authentication authentication)
    {
        LOG.debug("Get number of active Case Files by queue.");

        SearchResults searchResults = new SearchResults();

        List<Object> queues = getActiveCaseFileByQueueService().getQueues(authentication, searchResults);
        List<Object> facet = getActiveCaseFileByQueueService().getFacet(authentication, searchResults);

        return getActiveCaseFileByQueueService().getNumberOfActiveCaseFilesByQueue(queues, facet);
    }

    public ActiveCaseFileByQueueService getActiveCaseFileByQueueService()
    {
        return activeCaseFileByQueueService;
    }

    public void setActiveCaseFileByQueueService(ActiveCaseFileByQueueService activeCaseFileByQueueService)
    {
        this.activeCaseFileByQueueService = activeCaseFileByQueueService;
    }
}
