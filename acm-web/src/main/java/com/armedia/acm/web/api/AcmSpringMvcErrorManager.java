package com.armedia.acm.web.api;

/*-
 * #%L
 * ACM Shared Web Artifacts
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

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmNotAuthorizedException;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.core.exceptions.AcmParticipantsException;
import com.armedia.acm.core.exceptions.AcmResourceNotFoundException;
import com.armedia.acm.core.exceptions.AcmResourceNotModifiableException;
import com.armedia.acm.core.exceptions.AcmStateOfArkcaseGenerateReportException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.core.exceptions.InvalidLookupException;
import com.armedia.acm.crypto.exceptions.AcmEncryptionBadKeyOrDataException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AcmSpringMvcErrorManager
{
    private Logger log = LogManager.getLogger(getClass());

    @ExceptionHandler(AcmParticipantsException.class)
    public void handleException(HttpServletResponse response, AcmParticipantsException e)
    {
        log.error("Participants exception: " + e.getMessage(), e);
        sendResponse(HttpStatus.BAD_REQUEST, response, e.getMessage());
    }

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

    @ExceptionHandler(AcmUpdateObjectFailedException.class)
    public void handleUpdateObjectFailed(HttpServletResponse response, AcmUpdateObjectFailedException e)
    {
        log.error("Update Object Failed: " + e.getMessage(), e);
        sendResponse(HttpStatus.BAD_REQUEST, response, e.getMessage());
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

    @ExceptionHandler(AcmResourceNotFoundException.class)
    public void resourceNotFound(HttpServletResponse response, Exception e)
    {
        log.error("Resource not found: " + e.getMessage(), e);
        sendResponse(HttpStatus.NOT_FOUND, response, e.getMessage());
    }

    @ExceptionHandler(AcmResourceNotModifiableException.class)
    public void resourceNotModifiable(HttpServletResponse response, Exception e)
    {
        log.error("Resource cannot be modified: " + e.getMessage(), e);
        sendResponse(HttpStatus.CONFLICT, response, e.getMessage());
    }

    @ExceptionHandler(InvalidLookupException.class)
    public void invalidLookup(HttpServletResponse response, Exception e)
    {
        log.error("Invalid lookup: " + e.getMessage(), e);
        sendResponse(HttpStatus.BAD_REQUEST, response, e.getMessage());
    }

    @ExceptionHandler(AcmObjectLockException.class)
    public void handleException(HttpServletResponse response, AcmObjectLockException e)
    {
        log.error("AcmObjectLockException occurred: " + e.getMessage(), e);
        sendResponse(HttpStatus.CONFLICT, response, e.getMessage());
    }

    @ExceptionHandler(AcmStateOfArkcaseGenerateReportException.class)
    public void handleAcmStateOfArkcaseGenerateReportException(HttpServletResponse response, AcmObjectLockException e)
    {
        log.error("AcmStateOfArkcaseGenerateReportException occurred", e.getMessage(), e);
        sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e.getMessage());
    }

    @ExceptionHandler(AcmAppErrorJsonMsg.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleJsonMessageError(HttpServletResponse response, AcmAppErrorJsonMsg e)
    {
        log.error("AcmAppErrorJsonMsg", e);
        Map<String, Object> result = new HashMap<>();
        result.put("message", e.getMessage());
        result.put("field", e.getField());
        result.put("objectType", e.getObjectType());
        result.put("extra", e.getExtra());
        return result;
    }

    protected void sendResponse(HttpStatus status, HttpServletResponse response, String message)
    {
        // Make sure the error message doesn't look like JSON when it really isn't JON
        // otherwise Angular will try to parse it and fail
        
        String jsonStart = "^(\\[|\\{)(.*)(\\]|\\})$";
        if (message != null && message.matches(jsonStart))
        {
            try
            {
                new JSONObject(message);
            }
            catch (JSONException e)
            {
                // check JSON Array as well
                try
                {
                    new JSONArray(message);
                }
                catch (JSONException e1)
                {
                    message = "." + message;
                }
            }
        }
        
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setStatus(status.value());

        boolean empty = message == null || message.trim().isEmpty();

        byte[] bytes = empty ? "Unknown Error...".getBytes() : message.getBytes();
        response.setContentLength(bytes.length);
        try
        {
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        }
        catch (IOException ie)
        {
            log.error("Could not send error response to client: " + ie.getMessage(), ie);
        }
    }

}
