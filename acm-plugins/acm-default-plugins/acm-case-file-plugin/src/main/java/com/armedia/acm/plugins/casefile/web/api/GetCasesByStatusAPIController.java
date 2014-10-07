package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseByStatusDto;
import com.armedia.acm.plugins.casefile.model.CasesByStatusAndTimePeriod;
import com.armedia.acm.plugins.casefile.model.TimePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by marjan.stefanoski on 9/3/2014.
 */
@Controller
@RequestMapping({"/api/v1/plugin/casebystatus", "/api/latest/plugin/casebystatus"})
public class GetCasesByStatusAPIController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private CaseFileDao caseFileDao;

    @RequestMapping(
            value ="/{timePeriod}",
            method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<CaseByStatusDto> getCasesByStatus(
            @PathVariable("timePeriod") String timePeriod,
            Authentication authentication
    ) throws AcmListObjectsFailedException

    {
        if (log.isInfoEnabled()){
            log.info("Getting all cases grouped by status");
        }
        List<CaseByStatusDto> retval = null;
        switch (CasesByStatusAndTimePeriod.getTimePeriod(timePeriod)) {
            case ALL:
                retval = getCaseFileDao().getAllCasesByStatus();
                break;
            case LAST_MONTH:
                retval = getCaseFileDao().getCasesByStatusAndByTimePeriod(TimePeriod.THIRTY_DAYS);
                break;
            case LAST_WEEK:
                retval = getCaseFileDao().getCasesByStatusAndByTimePeriod(TimePeriod.SEVEN_DAYS);
                break;
            case LAST_YEAR:
                retval = getCaseFileDao().getCasesByStatusAndByTimePeriod(TimePeriod.ONE_YEAR);
                break;
            default:
                retval = getCaseFileDao().getAllCasesByStatus();
                break;
        }
        return retval;

    }

    public CaseFileDao getCaseFileDao() {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }
}
