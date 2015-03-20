package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.search.service.ObjectMapperFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class FileUploadAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;

    private final String uploadFileType = "attachment";


    @RequestMapping(
            value = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EcmFile> uploadFile(
            @RequestParam("parentObjectType") String parentObjectType,
            @RequestParam("parentObjectId") Long parentObjectId,
            @RequestParam(value = "fileType", required = false, defaultValue = uploadFileType) String fileType,
            MultipartHttpServletRequest request,
            Authentication authentication,
            HttpSession session) throws AcmCreateObjectFailedException, AcmUserActionFailedException, IOException
    {
        List<EcmFile> uploaded = uploadFiles(authentication, parentObjectType, parentObjectId, fileType, request, session);
        return uploaded;
    }

    @RequestMapping(
            value = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "!" + MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String uploadFileForBrowsersWithoutFileUploadViaXHR(
            @RequestParam("parentObjectType") String parentObjectType,
            @RequestParam("parentObjectId") Long parentObjectId,
            @RequestParam(value = "fileType", required = false, defaultValue = uploadFileType) String fileType,
            MultipartHttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication,
            HttpSession session) throws AcmCreateObjectFailedException, AcmUserActionFailedException, IOException
    {
        String responseMimeType = MediaType.TEXT_PLAIN_VALUE;
        response.setContentType(responseMimeType);

        List<EcmFile> uploaded = uploadFiles(authentication, parentObjectType, parentObjectId, fileType, request, session);

        ObjectMapper om = new ObjectMapperFactory().createObjectMapper();
        String jsonUploadedFiles = om.writeValueAsString(uploaded);

        return jsonUploadedFiles;
    }

    protected List<EcmFile> uploadFiles(
            Authentication authentication,
            String parentObjectType,
            Long parentObjectId,
            String fileType,
            MultipartHttpServletRequest request,
            HttpSession session)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, IOException
    {

        String ipAddress = (String) session.getAttribute("acm_ip_address");

        AcmContainer container = getEcmFileService().getOrCreateContainerFolder(parentObjectType, parentObjectId);
        if ( container.getFolder() == null )
        {
            // not really possible since the cm_folder_id is not nullable.  But we'll account for it anyway
            throw new IllegalStateException("Container '" + container.getId() + "' does not have a folder!");
        }
        String folderId = container.getFolder().getCmisFolderId();

        //for multiple files
        MultiValueMap<String, MultipartFile> attachments = request.getMultiFileMap();

        List<EcmFile> uploadedFiles = new ArrayList<>();

        if ( attachments != null )
        {
            for ( Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet() )
            {
                final List<MultipartFile> attachmentsList = entry.getValue();

                if (attachmentsList != null && !attachmentsList.isEmpty() )
                {
                    for (final MultipartFile attachment : attachmentsList)
                    {
                        AcmMultipartFile f = new AcmMultipartFile(
                                attachment.getName(),
                                attachment.getOriginalFilename(),
                                attachment.getContentType(),
                                attachment.isEmpty(),
                                attachment.getSize(),
                                attachment.getBytes(),
                                attachment.getInputStream(),
                                true);

                        EcmFile temp = getEcmFileService().upload(
                                fileType,
                                f,
                                authentication,
                                folderId,
                                parentObjectType,
                                parentObjectId);
                        uploadedFiles.add(temp);

                        // TODO: audit events
                    }
                }
            }
        }


        return uploadedFiles;
    }

    @RequestMapping(value = "/{ecmFileId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteFile(
            @PathVariable("ecmFileId") String ecmFileId,
            Authentication authentication)
    {
        if ( log.isInfoEnabled() )
        {
            log.info("The user '" + authentication.getName() + "' deleted file: '" + ecmFileId + "'");
        }


        // since jQuery File Upload wants the file name as an attribute name, we have to build the JSON manually
        // (since we can't write a POJO to have field names of random file names)
        JSONObject retval = new JSONObject();
        JSONArray files = new JSONArray();
        retval.put("files", files);

        JSONObject deleted = new JSONObject();
        deleted.put("The File Name.txt", true);
        files.put(deleted);

        String filesString = retval.toString();

        return new ResponseEntity<>(filesString, HttpStatus.OK);

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
