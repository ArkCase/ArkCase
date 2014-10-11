package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
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

@Controller
@RequestMapping( { "/api/v1/plugin/casefile", "/api/latest/plugin/casefile"})
public class FindCaseFileEventsAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AuditDao dao;

    @RequestMapping(
            value = "/events/{caseId}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<AuditEvent> caseEvents(
            @PathVariable(value = "caseId") Long caseId,
            Authentication auth
    ) throws AcmUserActionFailedException
    {
        if (log.isDebugEnabled())
        {
            log.debug("List events for case id: '" + caseId + "'");
        }


        return getDao().findAuditsByEventPatternAndObjectId(
                "com.armedia.acm.casefile.event.",
                "CASE_FILE",
                caseId);

    }

    public AuditDao getDao()
    {
        return dao;
    }

    public void setDao(AuditDao dao)
    {
        this.dao = dao;
    }
}
