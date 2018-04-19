package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseFile;

/**
 * @author sasko.tanaskoski
 *
 */
public interface GetCaseByNumberService
{
    CaseFile getCaseByNumber(String caseNumber);
}
