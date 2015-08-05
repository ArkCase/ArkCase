package com.armedia.acm.userinterface.connector;

import com.armedia.acm.userinterface.model.UserInterfaceConstants;
import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.HttpConnector;
import org.springframework.extensions.webscripts.connector.RemoteClient;

/**
 * Created by riste.tutureski on 7/31/2015.
 */
public class ArkCaseConnector extends HttpConnector
{
    public ArkCaseConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }

    @Override
    protected void applyRequestAuthentication(RemoteClient remoteClient, ConnectorContext context)
    {
        String acmTicket = null;

        // Retrieving the ticket from the connector context is a special case for Flash based apps
        // that do not share the same session and get at user connector session information.
        // We don't need this but put it here for sure.
        if (context != null)
        {
            acmTicket = context.getParameters().get(UserInterfaceConstants.ACM_TICKET);
        }

        if (getConnectorSession() != null)
        {
            acmTicket = (String) getConnectorSession().getParameter(UserInterfaceConstants.ACM_TICKET);
        }

        if (acmTicket != null)
        {
            remoteClient.setTicket(acmTicket);
            remoteClient.setTicketName(UserInterfaceConstants.ACM_TICKET);
        }
    }
}