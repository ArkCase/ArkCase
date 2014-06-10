package com.armedia.acm.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by armdev on 6/3/14.
 */
public class AcmSpringMvcErrorManager
{
    public void sendErrorResponse(HttpStatus httpStatus, String message, HttpServletResponse response) throws IOException
    {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        byte[] bytes = message.getBytes();
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }
}
