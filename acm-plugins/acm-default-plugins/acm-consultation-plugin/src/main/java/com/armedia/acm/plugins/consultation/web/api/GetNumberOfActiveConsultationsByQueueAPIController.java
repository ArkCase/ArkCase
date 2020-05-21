package com.armedia.acm.plugins.consultation.web.api;

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

import com.armedia.acm.plugins.consultation.service.ActiveConsultationByQueueService;
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
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
@Controller
@RequestMapping({ "/api/v1/plugin/consultation/number/by/queue", "/api/latest/plugin/consultation/number/by/queue" })
public class GetNumberOfActiveConsultationsByQueueAPIController
{
    private Logger LOG = LogManager.getLogger(getClass());

    private ActiveConsultationByQueueService activeConsultationByQueueService;

    /**
     * REST api for retrieving active consultations by queue
     *
     * @param authentication
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public Map<String, Long> getNumberOfActiveConsultationsByQueue(Authentication authentication)
    {
        LOG.debug("Get number of active Consultations by queue.");

        SearchResults searchResults = new SearchResults();

        List<Object> queues = getActiveConsultationByQueueService().getQueues(authentication, searchResults);
        List<Object> facet = getActiveConsultationByQueueService().getFacet(authentication, searchResults);

        return getActiveConsultationByQueueService().getNumberOfActiveConsultationsByQueue(queues, facet);
    }

    public ActiveConsultationByQueueService getActiveConsultationByQueueService() {
        return activeConsultationByQueueService;
    }

    public void setActiveConsultationByQueueService(ActiveConsultationByQueueService activeConsultationByQueueService) {
        this.activeConsultationByQueueService = activeConsultationByQueueService;
    }
}
