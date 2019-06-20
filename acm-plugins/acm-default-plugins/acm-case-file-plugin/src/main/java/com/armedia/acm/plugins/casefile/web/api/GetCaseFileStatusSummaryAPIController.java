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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseSummaryByStatusAndTimePeriodDto;
import com.armedia.acm.plugins.casefile.model.TimePeriod;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 10/9/2014.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/casebystatus", "/api/latest/plugin/casebystatus" })
public class GetCaseFileStatusSummaryAPIController
{

    private final Logger log = LogManager.getLogger(getClass());
    private CaseFileDao caseFileDao;
    private CaseFileEventUtility caseFileEventUtility;

    @RequestMapping(value = "/summary", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<CaseSummaryByStatusAndTimePeriodDto> getCasesSummaryByStatusAndTimePeriod(
            Authentication authentication) throws AcmListObjectsFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Getting cases grouped by status in a different time periods");
        }
        List<CaseSummaryByStatusAndTimePeriodDto> retval = getCaseSummary();
        return retval;
    }

    private List<CaseSummaryByStatusAndTimePeriodDto> getCaseSummary()
    {
        List<CaseSummaryByStatusAndTimePeriodDto> caseSummaryByStatusAndTimePeriodDtos = new ArrayList<>();
        CaseSummaryByStatusAndTimePeriodDto caseSummaryByStatusAndTimePeriodDto = new CaseSummaryByStatusAndTimePeriodDto();
        for (TimePeriod tp : TimePeriod.values())
        {
            caseSummaryByStatusAndTimePeriodDto.setTimePeriod(tp.getnDays());
            caseSummaryByStatusAndTimePeriodDto.setCaseByStatusDtoList(getCaseFileDao().getCasesByStatusAndByTimePeriod(tp));

            caseSummaryByStatusAndTimePeriodDtos.add(caseSummaryByStatusAndTimePeriodDto);
            caseSummaryByStatusAndTimePeriodDto = new CaseSummaryByStatusAndTimePeriodDto();
        }

        return caseSummaryByStatusAndTimePeriodDtos;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }
}
