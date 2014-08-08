package com.armedia.acm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmNotAuthorizedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class AcmSpringMvcErrorManager
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(AcmObjectNotFoundException.class)
    public void handleException(HttpServletResponse response, AcmObjectNotFoundException e)
    {
        sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e.getMessage());
    }

    @ExceptionHandler(AcmUserActionFailedException.class)
    public void handleException(HttpServletResponse response, AcmUserActionFailedException e)
    {
        sendResponse(HttpStatus.BAD_REQUEST, response, e.getMessage());
    }

    @ExceptionHandler(AcmCreateObjectFailedException.class)
    public void handleCreateObjectFailed(HttpServletResponse response, AcmCreateObjectFailedException e)
    {
        sendResponse(HttpStatus.BAD_REQUEST, response, e.getMessage());
    }

    @ExceptionHandler(AcmListObjectsFailedException.class)
    public void handleListObjectsFailed(HttpServletResponse response, AcmListObjectsFailedException e)
    {
        sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e.getMessage());
    }

    @ExceptionHandler(AcmNotAuthorizedException.class)
    public void handleNotAuthorized(HttpServletResponse response, AcmNotAuthorizedException e)
    {
        sendResponse(HttpStatus.FORBIDDEN, response, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public void lastChanceHandler(HttpServletResponse response, Exception e)
    {
        log.error("Last Chance Handler: " + e.getMessage(), e);
        sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e.getMessage());
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
