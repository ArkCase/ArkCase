package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping( { "/api/v1/plugin/casefile", "/api/latest/plugin/casefile"})
public class ListCaseFilesAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private CaseFileDao caseFileDao;

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<CaseFile> get()
    {
        List<CaseFile> retval = getCaseFileDao().findCaseFiles();

        return retval;
    }

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public CaseFiles getXml()
    {
        List<CaseFile> cases = getCaseFileDao().findCaseFiles();
        CaseFiles retval = new CaseFiles();
        retval.setCaseFiles(cases);

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
