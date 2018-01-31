package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class FindCasesByTitleAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private CaseFileDao caseFileDao;

    @RequestMapping(method = RequestMethod.GET, value = "/byTitle/{title}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<List<CaseFile>> findCasesByTitle(
            @PathVariable(value = "title") String title,
            Authentication auth) throws AcmObjectNotFoundException
    {
        log.info("Trying to fetch Case Files by Title {}", title);
        try
        {
            return new ResponseEntity<>(getCaseFileDao().findByTitle(title), HttpStatus.OK);
        }
        catch (AcmObjectNotFoundException e)
        {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
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
