package com.armedia.acm.plugins.personnelsecurity.cvs.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.personnelsecurity.cvs.model.ClearanceVerificationSystemDeterminationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by armdev on 12/5/14.
 */
public class ClearanceVerificationSystemExportService
{
    private EcmFileService ecmFileService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String FILE_TYPE = "cvs export";

    public void exportDeterminationRecord(
            String adjudicatorId,
            Long caseId,
            String caseNumber,
            String caseCmisFolderId,
            String subjectLastName,
            String adjudicationOutcome
    )
    {
        boolean clearanceGranted = "GRANT_CLEARANCE".equals(adjudicationOutcome);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(adjudicatorId, adjudicatorId);

        // since this service is triggered by Activiti as soon as the adjudicator completes their task, we know
        // the adjudication date is today.
        Date adjudicationDate = new Date();

        // ... but our system does not capture the date of birth yet.
        Date subjectDateOfBirth = null;

        ClearanceVerificationSystemDeterminationRecord determinationRecord =
                new ClearanceVerificationSystemDeterminationRecord(
                        subjectLastName, subjectDateOfBirth, clearanceGranted, adjudicationDate);

        String recordText = determinationRecord.toString();

        InputStream recordInputStream = new ByteArrayInputStream(recordText.getBytes());

        try
        {
            // TODO: use JMS to handle this upload via Mule so we get some retry logic
            getEcmFileService().upload(FILE_TYPE, recordInputStream, "text/plain", "cvsExport.txt", auth, caseCmisFolderId,
                    "CASE_FILE", caseId, caseNumber);
        }
        catch (AcmCreateObjectFailedException e)
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
