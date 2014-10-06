package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by armdev on 8/29/14.
 */
public class SaveCaseService
{
    private CaseFileDao caseFileDao;
    private SaveCaseFileBusinessRule saveRule;
    private CaseFileEventUtility caseFileEventUtility;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    public CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress)
    {
        boolean newCase = in.getId() == null;
        if ( newCase )
        {
            in.setCreator(auth.getName());

        }

        in.setModified(new Date());
        in.setModifier(auth.getName());

        CaseFile retval = getCaseFileDao().save(in);

        log.info("Saving case: retval is null? " + ( retval == null));

        getSaveRule().applyRules(retval);

        if ( newCase )
        {
            getCaseFileEventUtility().raiseEvent(retval, "DRAFT", new Date(), ipAddress, auth.getName(), auth);
        }
                
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

    public SaveCaseFileBusinessRule getSaveRule()
    {
        return saveRule;
    }

    public void setSaveRule(SaveCaseFileBusinessRule saveRule)
    {
        this.saveRule = saveRule;
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
