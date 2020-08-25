package gov.privacy.web.api;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.Map;

import gov.privacy.service.SARService;

/**
 * @author aleksandar.bujaroski
 */
@RequestMapping({ "/api/v1/plugin/request",
        "/api/latest/plugin/request" })
public class GetNextAvailableRequestAPIController
{
    private SARService SARService;

    @RequestMapping(value = "/nextAvailableRequestInQueue/{queueId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Long> getNextAvailableRequest(
            @PathVariable(value = "queueId") String queueId,
            @RequestParam(value = "createdDate") String createdDate) throws ParseException
    {
        return getSARService().getNextAvailableRequestsInQueue(Long.valueOf(queueId), createdDate);
    }

    public SARService getSARService()
    {
        return SARService;
    }

    public void setSARService(SARService SARService)
    {
        this.SARService = SARService;
    }
}
