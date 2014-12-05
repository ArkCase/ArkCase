package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.dao.ChangeCaseStatusDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

@Controller
@RequestMapping( { "/api/v1/plugin/casefile", "/api/latest/plugin/casefile"})
public class FindCaseByIdAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private CaseFileDao caseFileDao;
    private ChangeCaseStatusDao changeCaseStatusDao;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/byId/{id}",
            produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public CaseFile findCaseById(
            @PathVariable(value = "id") Long id,
            Authentication auth
    ) throws AcmObjectNotFoundException
    {
        try
        {
            CaseFile retval = getCaseFileDao().find(id);

            if ( retval == null )
            {
                throw new PersistenceException("No such case file with id '" + id + "'");
            }
            
            ChangeCaseStatus changeCaseStatus = getChangeCaseStatusDao().findByCaseId(retval.getId());
            retval.setChangeCaseStatus(changeCaseStatus);

            return retval;
        }
        catch (PersistenceException e)
        {
            throw new AcmObjectNotFoundException("Case File", id, e.getMessage(), e);
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

	public ChangeCaseStatusDao getChangeCaseStatusDao() {
		return changeCaseStatusDao;
	}

	public void setChangeCaseStatusDao(ChangeCaseStatusDao changeCaseStatusDao) {
		this.changeCaseStatusDao = changeCaseStatusDao;
	}
}
