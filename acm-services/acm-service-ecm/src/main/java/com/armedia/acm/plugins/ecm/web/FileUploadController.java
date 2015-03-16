package com.armedia.acm.plugins.ecm.web;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmContainerFolder;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/file")
public class FileUploadController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;

    private final String uploadFileType = "attachment";


    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestParam("parentObjectType") String parentObjectType,
            @RequestParam("parentObjectId") Long parentObjectId,
            @RequestHeader("Accept") String acceptType,
            MultipartHttpServletRequest request,
            Authentication authentication) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {

        String contextPath = request.getServletContext().getContextPath();

        AcmContainerFolder folder = getEcmFileService().getOrCreateContainerFolder(parentObjectType, parentObjectId);
        String folderId = folder.getCmisFolderId();

        //for multiple files
        MultiValueMap<String, MultipartFile> attachments = request.getMultiFileMap();

        List<Object> uploadedFilesJSON = new ArrayList<>();

        if ( attachments != null )
        {
            for ( Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet() )
            {
                final List<MultipartFile> attachmentsList = entry.getValue();

                if (attachmentsList != null && !attachmentsList.isEmpty() )
                {
                    for (final MultipartFile attachment : attachmentsList)
                    {
                        ResponseEntity<?> temp = getEcmFileService().upload(
                                uploadFileType,
                                attachment,
                                acceptType,
                                contextPath,
                                authentication,
                                folderId,
                                parentObjectType,
                                parentObjectId);
                        uploadedFilesJSON.add(temp.getBody());
                    }
                }
            }
        }
        return new ResponseEntity<Object>(uploadedFilesJSON, HttpStatus.OK);
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
