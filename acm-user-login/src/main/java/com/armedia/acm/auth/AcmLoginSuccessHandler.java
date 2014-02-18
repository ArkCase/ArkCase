package com.armedia.acm.auth;


import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AcmLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
{
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws ServletException, IOException
    {
        String userId = authentication.getName();

        HttpSession session = request.getSession(true);
        session.setAttribute("acm_username", userId);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
