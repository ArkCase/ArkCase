package com.armedia.acm.plugins.complaint.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.service.ComplaintEventPublisher;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping( { "/api/v1/plugin/complaint", "/api/latest/plugin/complaint"} )
public class AddFileAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ComplaintDao complaintDao;
    private EcmFileService ecmFileService;
    private ComplaintEventPublisher eventPublisher;

    private final String uploadFileType = "attachment";

    @RequestMapping(value = "/file", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    public ResponseEntity<? extends Object> uploadFile(
            @RequestParam("complaintId") Long complaintId,
            @RequestParam("files[]") MultipartFile file,
            @RequestHeader("Accept") String acceptType,
            HttpServletRequest request,
            Authentication authentication) throws AcmCreateObjectFailedException, AcmObjectNotFoundException
    {

        if ( log.isInfoEnabled() )
        {
            log.info("Adding file to complaint id " + complaintId);
        }
        Complaint in = null;
        try
        {
             in = getComplaintDao().find(complaintId);

            if ( in == null )
            {
                throw new AcmObjectNotFoundException("complaint", complaintId, "No Such Complaint", null);
            }


            String folderId = in.getContainerFolder().getCmisFolderId();
            String objectType = "COMPLAINT";
            Long objectId = complaintId;

            String contextPath = request.getServletContext().getContextPath();

            ResponseEntity<? extends Object> responseEntity =  getEcmFileService().
                    upload(uploadFileType, file, acceptType, contextPath, authentication, folderId,
                    objectType, objectId);

            getEventPublisher().publishComplaintFileAddedEvent(in,authentication.getName(),true);

            return responseEntity;
        }
        catch (PersistenceException e)
        {
            getEventPublisher().publishComplaintFileAddedEvent(in,authentication.getName(), false);
            throw new AcmObjectNotFoundException("complaint", complaintId, e.getMessage(), e);
        }
    }


    public ComplaintEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(ComplaintEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
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
