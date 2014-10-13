package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseByStatusDto;
import com.armedia.acm.plugins.casefile.model.CaseSummaryByStatusAndTimePeriodDto;
import com.armedia.acm.plugins.casefile.model.TimePeriod;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 10/9/2014.
 */
@Controller
@RequestMapping({"/api/v1/plugin/casebystatus", "/api/latest/plugin/casebystatus"})
public class GetCaseFileStatusSummaryAPIController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private CaseFileDao caseFileDao;
    private CaseFileEventUtility caseFileEventUtility;

    @RequestMapping(
            value = "/summary",
            method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public List<CaseSummaryByStatusAndTimePeriodDto> getCasesSummaryByStatusAndTimePeriod(
            Authentication authentication
    ) throws AcmListObjectsFailedException {
        if (log.isInfoEnabled()) {
            log.info("Getting cases grouped by status in a different time periods");
        }
        List<CaseSummaryByStatusAndTimePeriodDto> retval = getCaseSummary();
        return retval;
    }

    private List<CaseSummaryByStatusAndTimePeriodDto> getCaseSummary(){
         List<CaseSummaryByStatusAndTimePeriodDto> caseSummaryByStatusAndTimePeriodDtos = new ArrayList<CaseSummaryByStatusAndTimePeriodDto>();
         CaseSummaryByStatusAndTimePeriodDto caseSummaryByStatusAndTimePeriodDto = new CaseSummaryByStatusAndTimePeriodDto();
        for(TimePeriod tp: TimePeriod.values()) {
             caseSummaryByStatusAndTimePeriodDto.setTimePeriod(tp.getnDays());
             caseSummaryByStatusAndTimePeriodDto.setCaseByStatusDtoList(getCaseFileDao().getCasesByStatusAndByTimePeriod(tp));

             caseSummaryByStatusAndTimePeriodDtos.add(caseSummaryByStatusAndTimePeriodDto);
             caseSummaryByStatusAndTimePeriodDto = new CaseSummaryByStatusAndTimePeriodDto();
         }

        return caseSummaryByStatusAndTimePeriodDtos;
    }

    public CaseFileDao getCaseFileDao() {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    public CaseFileEventUtility getCaseFileEventUtility() {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility) {
        this.caseFileEventUtility = caseFileEventUtility;
    }
}
