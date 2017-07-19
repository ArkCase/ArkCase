package com.armedia.acm.services.email.service;

import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import org.springframework.security.core.Authentication;

/**
 * Created by manoj.dhungana on 7/14/2017.
 */
public class AcmEmailContentGeneratorService
{
    private AuthenticationTokenService authenticationTokenService;
    private AuthenticationTokenDao authenticationTokenDao;

    public String generateEmailBody(EmailWithEmbeddedLinksDTO emailDTO, String emailAddress, Authentication authentication)
    {
        StringBuilder body = new StringBuilder();
        body.append(emailDTO.getBody() != null ? emailDTO.getBody() : "").append("<br/>");

        if (emailDTO.getFileIds() != null)
        {
            for (Long fileId : emailDTO.getFileIds())
            {
                String token = generateAndSaveAuthenticationToken(fileId, emailAddress, emailDTO, authentication);
                body.append(emailDTO.getBaseUrl()).append(fileId).append("&acm_email_ticket=").append(token).append("<br/>");
            }
        }

        return emailDTO.buildMessageBodyFromTemplate(body.toString());
    }

    private String generateAndSaveAuthenticationToken(Long fileId, String emailAddress, EmailWithEmbeddedLinksDTO emailDTO,
                                                      Authentication authentication)
    {
        String token = getAuthenticationTokenService().getUncachedTokenForAuthentication(authentication);
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setKey(token);
        authenticationToken.setStatus(AuthenticationTokenConstants.ACTIVE);
        authenticationToken.setEmail(emailAddress);
        authenticationToken.setFileId(fileId);
        getAuthenticationTokenDao().save(authenticationToken);
        return token;
    }

    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    public AuthenticationTokenDao getAuthenticationTokenDao()
    {
        return authenticationTokenDao;
    }

    public void setAuthenticationTokenDao(AuthenticationTokenDao authenticationTokenDao)
    {
        this.authenticationTokenDao = authenticationTokenDao;
    }
}
