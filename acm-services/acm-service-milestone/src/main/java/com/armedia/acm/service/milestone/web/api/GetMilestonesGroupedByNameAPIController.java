package com.armedia.acm.service.milestone.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.service.milestone.dao.MilestoneDao;
import com.armedia.acm.service.milestone.model.MilestoneByNameDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping({"/api/v1/milestonebyname", "/api/latest/milestonebyname"})
public class GetMilestonesGroupedByNameAPIController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private MilestoneDao  milestoneDao;

    @RequestMapping(value= "/{objectType}",method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<MilestoneByNameDto> getMilestonesGroupedByDate(
            @PathVariable("objectType") String objectType,
            Authentication authentication,
           HttpSession session ) throws AcmListObjectsFailedException {
        if (log.isInfoEnabled()){
            log.info("Getting milestones grouped by date");
        }
        List<MilestoneByNameDto> result = getMilestoneDao().getAllMilestonesForCaseFilesGroupedByName(objectType);
        return result;
    }

    public MilestoneDao getMilestoneDao() {
        return milestoneDao;
    }

    public void setMilestoneDao(MilestoneDao milestoneDao) {
        this.milestoneDao = milestoneDao;
    }
}
