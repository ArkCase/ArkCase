package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;

/**
 * @author sasko.tanaskoski
 *
 */
public class GetCaseByNumberServiceImpl implements GetCaseByNumberService
{

    private CaseFileDao caseFileDao;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.plugins.casefile.service.GetCaseByNumberService#getCaseByNumber(java.lang.String)
     */
    @Override
    public CaseFile getCaseByNumber(String caseNumber)
    {
        return getCaseFileDao().findByCaseNumber(caseNumber);
    }

    /**
     * @return the caseFileDao
     */
    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    /**
     * @param caseFileDao
     *            the caseFileDao to set
     */
    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

}
