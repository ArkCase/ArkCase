package com.armedia.acm.userinterface.connector;

import com.armedia.acm.userinterface.connector.model.UserInterfaceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.ConnectorSession;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.CredentialsImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by riste.tutureski on 8/5/2015.
 */
public class ArkCaseAuthenticationProvider implements AuthenticationProvider {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private ConnectorService connectorService;
    private ArkCaseAuthenticator arkCaseAuthenticator;
    private String acmServicesUrl;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        ConnectorSession connectorSession = getConnectorService().getConnectorSession(getSession(), "arkcase");
        Credentials credentials = new CredentialsImpl(connectorSession.getEndpointId());
        credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
        credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, password);

        try
        {
            connectorSession = getArkCaseAuthenticator().authenticate(getAcmServicesUrl(), credentials, connectorSession);
            String ticket = connectorSession.getParameter(UserInterfaceConstants.ACM_TICKET);
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER_INTERFACE"));

            Authentication auth = new UsernamePasswordAuthenticationToken(connectorSession, password, grantedAuthorities);

            return auth;
        }
        catch (Exception e)
        {
            LOG.error("Error while authenticating ... ", e);
            AuthenticationException authExc = new AuthenticationException("Unable to authenticate on ArkCase system") {};
            throw authExc;
        }
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private HttpSession getSession()
    {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        if (servletRequestAttributes != null && servletRequestAttributes.getRequest() != null)
        {
            return servletRequestAttributes.getRequest().getSession();
        }

        return null;
    }

    public ConnectorService getConnectorService()
    {
        return connectorService;
    }

    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }

    public ArkCaseAuthenticator getArkCaseAuthenticator()
    {
        return arkCaseAuthenticator;
    }

    public void setArkCaseAuthenticator(ArkCaseAuthenticator arkCaseAuthenticator)
    {
        this.arkCaseAuthenticator = arkCaseAuthenticator;
    }

    public String getAcmServicesUrl()
    {
        return acmServicesUrl;
    }

    public void setAcmServicesUrl(String acmServicesUrl)
    {
        this.acmServicesUrl = acmServicesUrl;
    }
}
