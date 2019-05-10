package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;

/**
 * @author aleksandar.bujaroski
 */
public class GetCaseServiceImpl implements GetCaseService {

    private CaseFileDao caseFileDao;

    @Override
    public CaseFile getCaseById(long id) {
        return getCaseFileDao().find(id);
    }

    public CaseFileDao getCaseFileDao() {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }
}
