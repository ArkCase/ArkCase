package com.armedia.acm.plugins.ecm.web;

import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/file")
public class FileUploadController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<? extends Object> uploadFile(
            @RequestParam("files[]") MultipartFile file,
            @RequestParam("cmisFolderId") String cmisFolderId,
            @RequestParam("parentObjectType") String parentObjectType,
            @RequestParam("parentObjectId") Long parentObjectId,
            @RequestParam("parentObjectName") String parentObjectName,
            @RequestHeader("Accept") String acceptType,
            HttpServletRequest request,
            Authentication authentication)
    {

        String contextPath = request.getServletContext().getContextPath();
        return getEcmFileService().upload(file, acceptType, contextPath, authentication, cmisFolderId,
                parentObjectType, parentObjectId, parentObjectName);

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
