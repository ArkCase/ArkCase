package com.armedia.acm.web.api;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmEncryptionBadKeyOrDataException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmNotAuthorizedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@ControllerAdvice
public class AcmSpringMvcErrorManager
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(AcmObjectNotFoundException.class)
    public void handleException(HttpServletResponse response, AcmObjectNotFoundException e)
    {
        log.error("Object Not Found: " + e.getMessage(), e);
        sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e.getMessage());
    }

    @ExceptionHandler(AcmUserActionFailedException.class)
    public void handleException(HttpServletResponse response, AcmUserActionFailedException e)
    {
        log.error("User Action Failed: " + e.getMessage(), e);
        sendResponse(HttpStatus.BAD_REQUEST, response, e.getMessage());
    }

    @ExceptionHandler(AcmCreateObjectFailedException.class)
    public void handleCreateObjectFailed(HttpServletResponse response, AcmCreateObjectFailedException e)
    {
        log.error("Create Object Failed: " + e.getMessage(), e);
        sendResponse(HttpStatus.BAD_REQUEST, response, e.getMessage());
    }

    @ExceptionHandler(AcmListObjectsFailedException.class)
    public void handleListObjectsFailed(HttpServletResponse response, AcmListObjectsFailedException e)
    {
        log.error("List Objects Failed: " + e.getMessage(), e);
        sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e.getMessage());
    }

    @ExceptionHandler(AcmNotAuthorizedException.class)
    public void handleNotAuthorized(HttpServletResponse response, AcmNotAuthorizedException e)
    {
        log.error("Not Authorized: " + e.getMessage(), e);
        sendResponse(HttpStatus.FORBIDDEN, response, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void accessDeniedHandler(HttpServletResponse response, AccessDeniedException e)
    {
        log.error("Access is not granted: " + e.getMessage(), e);
        sendResponse(HttpStatus.FORBIDDEN, response, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public void lastChanceHandler(HttpServletResponse response, Exception e)
    {
        log.error("Last Chance Handler: " + e.getMessage(), e);
        sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e.getMessage());
    }

    @ExceptionHandler(AcmEncryptionBadKeyOrDataException.class)
    public void invalidOutlookPassword(HttpServletResponse response, Exception e)
    {
        log.error("Invalid outlook password: " + e.getMessage(), e);
        sendResponse(HttpStatus.BAD_REQUEST, response, e.getMessage());
    }

    @ExceptionHandler(AcmOutlookItemNotFoundException.class)
    public void outlookItemNotFound(HttpServletResponse response, Exception e)
    {
        log.error("Requested item not found: " + e.getMessage(), e);
        sendResponse(HttpStatus.NOT_FOUND, response, e.getMessage());
    }

    @ExceptionHandler(AcmAppErrorJsonMsg.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleJsonMessageError(HttpServletResponse response, AcmAppErrorJsonMsg e)
    {
        log.error("AcmAppErrorJsonMsg", e);
        Map<String, Object> result = new HashedMap();
        result.put("message", e.getMessage());
        result.put("field", e.getField());
        result.put("objectType", e.getObjectType());
        return result;
    }

    protected void sendResponse(HttpStatus status, HttpServletResponse response, String message)
    {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setStatus(status.value());

        boolean empty = message == null || message.trim().isEmpty();

        byte[] bytes = empty ? "Unknown Error...".getBytes() : message.getBytes();
        response.setContentLength(bytes.length);
        try
        {
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (IOException ie)
        {
            log.error("Could not send error response to client: " + ie.getMessage(), ie);
        }
    }

}
