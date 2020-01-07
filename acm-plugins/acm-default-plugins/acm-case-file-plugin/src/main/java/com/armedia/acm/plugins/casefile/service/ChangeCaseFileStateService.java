package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.dao.ChangeCaseStatusDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by armdev on 9/6/14.
 */
public class ChangeCaseFileStateService
{
    private final Logger log = LogManager.getLogger(getClass());
    private CaseFileDao dao;
    private ChangeCaseStatusDao changeCaseStatusDao;
    private CaseFileEventUtility caseFileEventUtility;
    private PipelineManager<ChangeCaseStatus, CaseFilePipelineContext> pipelineManager;

    @Transactional
    public void save(ChangeCaseStatus form, Authentication auth, String mode) throws PipelineProcessException
    {
        CaseFilePipelineContext ctx = new CaseFilePipelineContext();
        ctx.setAuthentication(auth);
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        ctx.setIpAddress(ipAddress);
        ctx.addProperty("mode", mode);
        ctx.addProperty("caseResolution", form.getCaseResolution());
        ctx.addProperty("changeDate", form.getChangeDate().toString());

        pipelineManager.executeOperation(form, ctx, () -> {

            ChangeCaseStatus savedCaseStatus = getChangeCaseStatusDao().save(form);
            ctx.setChangeCaseStatus(savedCaseStatus);
            return savedCaseStatus;
        });
    }

    public CaseFile changeCaseState(Authentication auth, Long caseId, String newState, String ipAddress)
            throws AcmUserActionFailedException
    {
        try
        {
            log.info("Case ID : [{}] and incoming status is : [{}]", caseId, newState);
            CaseFile retval = getDao().find(caseId);

            // do we need to do anything?
            if (retval.getStatus().equals(newState))
            {
                return retval;
            }

            Date now = new Date();

            retval.setStatus(newState);

            if ("CLOSED".equals(newState))
            {
                retval.setClosed(now);
            }

            retval = getDao().save(retval);

            log.info("Case ID : [{}] and saved status is : [{}]", caseId, retval.getStatus());

            getCaseFileEventUtility().raiseEvent(retval, newState, now, ipAddress, auth.getName(), auth);

            return retval;
        }
        catch (Exception e)
        {
            throw new AcmUserActionFailedException("Set case to " + newState, "Case File", caseId, e.getMessage(), e);
        }
    }

    public void handleChangeCaseStatusApproved(Long caseId, Long requestId, String userId, Date approvalDate, String ipAddress)
    {
        CaseFile updatedCase = updateCaseStatus(caseId, requestId);

        updateCaseStatusRequestToApproved(requestId);

        getCaseFileEventUtility().raiseEvent(updatedCase, updatedCase.getStatus(), approvalDate, ipAddress, userId, null);
    }

    private CaseFile updateCaseStatus(Long caseId, Long requestId)
    {
        ChangeCaseStatus changeCaseStatus = getChangeCaseStatusDao().find(requestId);

        CaseFile toSave = getDao().find(caseId);
        toSave.setStatus(changeCaseStatus.getStatus());

        CaseFile updated = getDao().save(toSave);

        return updated;
    }

    private ChangeCaseStatus updateCaseStatusRequestToApproved(Long id)
    {
        ChangeCaseStatus toSave = getChangeCaseStatusDao().find(id);
        toSave.setStatus("APPROVED");

        ChangeCaseStatus updated = getChangeCaseStatusDao().save(toSave);

        return updated;
    }

    public CaseFileDao getDao()
    {
        return dao;
    }

    public void setDao(CaseFileDao dao)
    {
        this.dao = dao;
    }

    public ChangeCaseStatusDao getChangeCaseStatusDao()
    {
        return changeCaseStatusDao;
    }

    public void setChangeCaseStatusDao(ChangeCaseStatusDao changeCaseStatusDao)
    {
        this.changeCaseStatusDao = changeCaseStatusDao;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }

    public PipelineManager<ChangeCaseStatus, CaseFilePipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<ChangeCaseStatus, CaseFilePipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }
}
