package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by marjan.stefanoski on 08.11.2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/casefile", "/api/latest/plugin/casefile"} )
public class AddFileToCaseAPIController {
    private Logger log = LoggerFactory.getLogger(getClass());

    private CaseFileDao caseFileDao;
    private EcmFileService ecmFileService;

    private final String uploadFileType = "attachment_case";

    @RequestMapping(value = "/file", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public ResponseEntity<? extends Object> uploadFile(
            @RequestParam("caseFileId") Long caseId,
            @RequestParam("files[]") MultipartFile file,
            @RequestHeader("Accept") String acceptType,
            HttpServletRequest request,
            Authentication authentication) throws AcmCreateObjectFailedException, AcmObjectNotFoundException
    {

        if ( log.isInfoEnabled() )
        {
            log.info("Adding file to case id " + caseId);
        }

        try
        {
            CaseFile in = getCaseFileDao().find(caseId);

            if ( in == null )
            {
                throw new AcmObjectNotFoundException("case", caseId, "No Such Case", null);
            }


            String folderId = in.getEcmFolderId();
            String objectType = "CASE";
            Long objectId = caseId;
            String objectName = in.getCaseNumber();

            String contextPath = request.getServletContext().getContextPath();

            return getEcmFileService().upload(uploadFileType, file, acceptType, contextPath, authentication, folderId,
                    objectType, objectId, objectName);
        }
        catch (PersistenceException e)
        {
            throw new AcmObjectNotFoundException("case", caseId, e.getMessage(), e);
        }
    }

    public CaseFileDao getCaseFileDao() {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public String getUploadFileType() {
        return uploadFileType;
    }
}
