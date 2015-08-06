package com.armedia.acm.userinterface.connector;

import com.armedia.acm.userinterface.connector.model.UserInterfaceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.surf.exception.AuthenticationException;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.AbstractAuthenticator;
import org.springframework.extensions.webscripts.connector.ConnectorSession;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.RemoteClient;
import org.springframework.extensions.webscripts.connector.Response;

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
                (pass = (String) credentials.getProperty(Credentials.CREDENTIAL_PASSWORD)) != null)
        {
            // Build a new remote client
            RemoteClient remoteClient = new RemoteClient(endpoint);

            if (LOG.isDebugEnabled())
            {
                LOG.debug("Authenticating user: " + user);
            }

            Map<String, String> requestProperties = new HashMap<String, String>();
            requestProperties.put("Accept", "text/plain");

            remoteClient.setUsernamePassword(user, pass);
            remoteClient.setRequestMethod(HttpMethod.GET);
            remoteClient.setRequestProperties(requestProperties);
            Response response = remoteClient.call(UserInterfaceConstants.API_LOGIN);

            // read back the ticket
            if (response.getStatus().getCode() == 200)
            {
                String ticket = null;
                try
                {
                    ticket = response.getResponse();
                }
                catch (Exception e)
                {
                    throw new AuthenticationException("Unable to retrieve login ticket from ArkCase", e);
                }

                if (LOG.isDebugEnabled())
                {
                    LOG.debug("Ticket: " + ticket);
                }

                // place the ticket back into the connector session
                if (connectorSession != null)
                {
                    connectorSession.setParameter(UserInterfaceConstants.ACM_TICKET, ticket);

                    // signal that this succeeded
                    cs = connectorSession;
                }
            }
            else if (response.getStatus().getCode() == Status.STATUS_NO_CONTENT)
            {
                if (LOG.isDebugEnabled())
                {
                    LOG.debug("SC_NO_CONTENT(204) status received - retreiving auth cookies...");
                }

                // The login created an empty response, probably with cookies in the connectorSession. We succeeded.
                processResponse(response, connectorSession);
                cs = connectorSession;
            }
            else
            {
                if (LOG.isDebugEnabled())
                {
                    LOG.debug("Authentication failed, received response code: " + response.getStatus().getCode());
                }
            }
        }
        else if (LOG.isDebugEnabled())
        {
            LOG.debug("No user credentials available - cannot authenticate.");
        }

        return cs;
    }

    @Override
    public boolean isAuthenticated(String endpoint, ConnectorSession connectorSession)
    {
        return (connectorSession.getParameter(UserInterfaceConstants.ACM_TICKET) != null) || (connectorSession.getCookieNames().length != 0);
    }
}
