package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.plugins.casefile.model.CaseFile;

/**
 * @author aleksandar.bujaroski
 */
public interface GetCaseService {
    CaseFile getCaseById(long id);
}
