package com.armedia.acm.service.milestone.web.api;

/*-
 * #%L
 * ACM Service: Milestones
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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.service.milestone.dao.MilestoneDao;
import com.armedia.acm.service.milestone.model.MilestoneByNameDto;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

/**
 * Created by marjan.stefanoski on 11.12.2014.
 */
@Controller
@RequestMapping({ "/api/v1/milestonebyname", "/api/latest/milestonebyname" })
public class GetMilestonesGroupedByNameAPIController
{

    private final Logger log = LogManager.getLogger(getClass());
    private MilestoneDao milestoneDao;

    @RequestMapping(value = "/{objectType}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<MilestoneByNameDto> getMilestonesGroupedByDate(
            @PathVariable("objectType") String objectType,
            Authentication authentication,
            HttpSession session) throws AcmListObjectsFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Getting milestones grouped by date");
        }
        List<MilestoneByNameDto> result = getMilestoneDao().getAllMilestonesForCaseFilesGroupedByName(objectType);
        return result;
    }

    public MilestoneDao getMilestoneDao()
    {
        return milestoneDao;
    }

    public void setMilestoneDao(MilestoneDao milestoneDao)
    {
        this.milestoneDao = milestoneDao;
    }
}
