package com.armedia.acm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class AcmSpringMvcErrorManager
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(AcmObjectNotFoundException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(HttpServletResponse response, AcmObjectNotFoundException e)
    {
        sendResponse(response, e.getMessage());
    }

    @ExceptionHandler(AcmUserActionFailedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void handleException(HttpServletResponse response, AcmUserActionFailedException e)
    {
        sendResponse(response, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void lastChanceHandler(HttpServletResponse response, Exception e)
    {
        sendResponse(response, e.getMessage());
    }



    protected void sendResponse(HttpServletResponse response, String message)
    {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        byte[] bytes = "".equals(message) ? "Unknown Error...".getBytes() : message.getBytes();
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


    public void sendErrorResponse(HttpStatus httpStatus, String message, HttpServletResponse response) throws IOException
    {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        byte[] bytes = message == null ? "Unknown Error...".getBytes() : message.getBytes();
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }
}
