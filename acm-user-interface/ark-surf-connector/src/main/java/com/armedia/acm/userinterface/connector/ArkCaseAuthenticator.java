package com.armedia.acm.userinterface.connector;

import com.armedia.acm.userinterface.connector.exception.ServiceUnavailableException;
import com.armedia.acm.userinterface.connector.model.UserInterfaceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.connector.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by riste.tutureski on 7/31/2015.
 */
public class ArkCaseAuthenticator extends AbstractAuthenticator
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public ConnectorSession authenticate(String endpoint, Credentials credentials, ConnectorSession connectorSession) throws AuthenticationException
    {
        ConnectorSession cs = null;

        String user = null;
        String pass = null;

        if (credentials != null &&
                (user = (String) credentials.getProperty(Credentials.CREDENTIAL_USERNAME)) != null &&
                (pass = (String) credentials.getProperty(Credentials.CREDENTIAL_PASSWORD)) != null &&
                !user.isEmpty() && !pass.isEmpty())
        {
            // Build a new remote client
            RemoteClient remoteClient = new RemoteClient(endpoint);

            LOG.debug("Authenticating user: " + user);

            Map<String, String> requestProperties = new HashMap<String, String>();
            requestProperties.put("Accept", "text/plain");

            remoteClient.setUsernamePassword(user, pass);
            remoteClient.setRequestMethod(HttpMethod.GET);
            remoteClient.setRequestProperties(requestProperties);
            Response response = remoteClient.call(UserInterfaceConstants.API_LOGIN);

            LOG.debug("Response Code: " + response.getStatus().getCode());

            switch (response.getStatus().getCode())
            {
                case 200:
                    cs = doAuthentication(response, connectorSession);
                    break;
                case 401:
                    LOG.error(UserInterfaceConstants.ERROR_MESSAGE_401);
                    throw new BadCredentialsException(UserInterfaceConstants.ERROR_MESSAGE_401) {};
                case 404:
                    LOG.error(UserInterfaceConstants.ERROR_MESSAGE_404);
                    throw new ServiceUnavailableException(UserInterfaceConstants.ERROR_MESSAGE_404) {};
                default:
                    LOG.error(UserInterfaceConstants.ERROR_MESSAGE_DEFAULT);
                    throw new AuthenticationException(UserInterfaceConstants.ERROR_MESSAGE_DEFAULT) {};
            }
        }
        else
        {
            LOG.error(UserInterfaceConstants.ERROR_MESSAGE_NO_CREDENTIALS);
            throw new BadCredentialsException(UserInterfaceConstants.ERROR_MESSAGE_NO_CREDENTIALS){};
        }

        return cs;
    }

    @Override
    public boolean isAuthenticated(String endpoint, ConnectorSession connectorSession)
    {
        return (connectorSession.getParameter(UserInterfaceConstants.ACM_TICKET) != null) || (connectorSession.getCookieNames().length != 0);
    }

    private ConnectorSession doAuthentication(Response response, ConnectorSession connectorSession) throws AuthenticationException
    {
        try
        {
            String ticket = response.getResponse();
            LOG.debug("Ticket: " + ticket);

            if (ticket == null || ticket.isEmpty())
            {
                throw new AuthenticationException(UserInterfaceConstants.ERROR_MESSAGE_NO_TICKET){};
            }

            connectorSession.setParameter(UserInterfaceConstants.ACM_TICKET, ticket);

        }
        catch (Exception e)
        {
            throw new AuthenticationException(UserInterfaceConstants.ERROR_MESSAGE_NO_TICKET){};
        }

        return connectorSession;
    }
}