package com.armedia.acm.plugins.personnelsecurity.cvs.service;

/*-
 * #%L
 * ACM Personnel Security
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.personnelsecurity.cvs.model.ClearanceVerificationSystemDeterminationRecord;
import com.armedia.acm.plugins.personnelsecurity.cvs.model.PersonnelSecurityConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by armdev on 12/5/14.
 */
public class ClearanceVerificationSystemExportService
{
    private final Logger log = LogManager.getLogger(getClass());
    private EcmFileService ecmFileService;

    public void exportDeterminationRecord(
            String adjudicatorId,
            Long caseId,
            String caseCmisFolderId,
            String subjectLastName,
            String adjudicationOutcome)
    {
        boolean clearanceGranted = PersonnelSecurityConstants.ADJUDICATION_OUTCOME_GRANT_CLEARANCE.equals(adjudicationOutcome);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(adjudicatorId, adjudicatorId);

        // since this service is triggered by Activiti as soon as the adjudicator completes their task, we know
        // the adjudication date is today.
        Date adjudicationDate = new Date();

        // ... but our system does not capture the date of birth yet.
        Date subjectDateOfBirth = null;

        ClearanceVerificationSystemDeterminationRecord determinationRecord = new ClearanceVerificationSystemDeterminationRecord(
                subjectLastName, subjectDateOfBirth, clearanceGranted, adjudicationDate);

        String recordText = determinationRecord.toString();

        InputStream recordInputStream = new ByteArrayInputStream(recordText.getBytes());

        try
        {
            // TODO: use JMS to handle this upload via Mule so we get some retry logic
            getEcmFileService().upload(
                    PersonnelSecurityConstants.CVS_FILE_NAME,
                    PersonnelSecurityConstants.CVS_FILE_TYPE,
                    PersonnelSecurityConstants.CVS_FILE_CATEGORY,
                    recordInputStream,
                    PersonnelSecurityConstants.CVS_FILE_MIME_TYPE,
                    PersonnelSecurityConstants.CVS_FILE_NAME,
                    auth,
                    caseCmisFolderId,
                    "CASE_FILE",
                    caseId);
        }
        catch (AcmCreateObjectFailedException | AcmUserActionFailedException e)
        {
            log.error("Could not create CVS export: " + e.getMessage(), e);
        }

    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
