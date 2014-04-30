package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.FileUpload;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping( { "/api/v1/plugin/complaint", "/api/latest/plugin/complaint"} )
public class AddFileAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ComplaintDao complaintDao;
    private EcmFileTransaction ecmFileTransaction;

    @RequestMapping(value = "/file", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFile(
            @RequestParam("complaintId") Long complaintId,
            @RequestParam("files[]") MultipartFile file,
            @RequestHeader("Accept") String acceptType,
            HttpServletRequest request,
            Authentication authentication)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Single files");
            log.debug("Accept header: '" + acceptType + "'");
        }

        if ( log.isInfoEnabled() )
        {
            log.info("The user '" + authentication.getName() + "' uploaded file: '" + file.getOriginalFilename() + "'");
            log.info("File size: " + file.getSize() + "; content type: " + file.getContentType());
        }

        HttpHeaders responseHeaders = getEcmFileTransaction().contentTypeFromAcceptHeader(acceptType);

        String contextPath = request.getServletContext().getContextPath();
        log.debug("context path: '" + contextPath + "'");

        Complaint in = getComplaintDao().find(Complaint.class, complaintId);

        try
        {
            EcmFile uploaded = getEcmFileTransaction().addFileTransaction(
                    authentication,
                    file.getInputStream(),
                    file.getContentType(),
                    file.getOriginalFilename(),
                    in.getEcmFolderId(),
                    "COMPLAINT",
                    in.getComplaintId(),
                    in.getComplaintNumber());

            FileUpload fileUpload = getEcmFileTransaction().fileUploadFromEcmFile(file, contextPath, uploaded);

            String json = getEcmFileTransaction().constructJqueryFileUploadJson(fileUpload);


            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        } catch (IOException | MuleException e)
        {
            log.error("Could not upload file: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }



    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public EcmFileTransaction getEcmFileTransaction()
    {
        return ecmFileTransaction;
    }

    public void setEcmFileTransaction(EcmFileTransaction ecmFileTransaction)
    {
        this.ecmFileTransaction = ecmFileTransaction;
    }
}
