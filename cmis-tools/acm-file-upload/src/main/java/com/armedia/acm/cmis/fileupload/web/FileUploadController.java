package com.armedia.acm.cmis.fileupload.web;

import com.armedia.acm.cmis.fileupload.model.FileUpload;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequestMapping("/file")
public class FileUploadController
{
    private Logger log = LoggerFactory.getLogger(getClass());


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(
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

        HttpHeaders responseHeaders = contentTypeFromAcceptHeader(acceptType);

        String contextPath = request.getServletContext().getContextPath();
        log.debug("context path: '" + contextPath + "'");

        FileUpload fileUpload = new FileUpload();
        fileUpload.setDeleteUrl(contextPath + "/file/12345");
        fileUpload.setName(file.getOriginalFilename());
        fileUpload.setSize(file.getSize());
        fileUpload.setUrl(contextPath + "/file/12345");

        List<FileUpload> retval = Collections.singletonList(fileUpload);
        Map retMap = Collections.singletonMap("files", retval);

        ObjectMapper om = new ObjectMapper();
        try
        {
            String json = om.writeValueAsString(retMap);
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        } catch (IOException e)
        {
            log.error("Could not generate json: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    private HttpHeaders contentTypeFromAcceptHeader(String acceptType)
    {
        HttpHeaders responseHeaders = new HttpHeaders();

        // since IE is broken we have to conditionally set the response content type
        if ( acceptType.contains(MediaType.APPLICATION_JSON_VALUE) || acceptType.contains("text/javascript"))
        {
            log.debug("sending javascript via response entity");
            // good browsers
            responseHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        }
        else
        {
            // ie
            log.debug("sending text plain via response entity");
            responseHeaders.add("Content-Type", MediaType.TEXT_PLAIN_VALUE);
        }
        return responseHeaders;
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
}
