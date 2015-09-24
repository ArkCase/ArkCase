package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by riste.tutureski on 9/22/2015.
 */
@Controller
@RequestMapping({"/api/v1/plugin/casefile/number/by/queue", "/api/latest/plugin/casefile/number/by/queue"})
public class GetNumberOfActiveOrdersByQueueAPIController
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    private CaseFileDao caseFileDao;

    /**
     * REST api for retrieving active orders by queue
     *
     * @param authentication
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public Map<String, Long> getNumberOfActiveOrdersByQueue(Authentication authentication)
    {
        LOG.debug("Get number of active orders by queue.");

        Map<String, Long> retval = getCaseFileDao().getNumberOfActiveOrdersByQueue();

        return retval;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}
