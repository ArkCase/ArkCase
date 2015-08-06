package com.armedia.acm.userinterface.connector.model;

/**
 * Created by riste.tutureski on 7/31/2015.
 */
public interface UserInterfaceConstants
{
    String ACM_TICKET = "acm_ticket";
    String API_LOGIN = "/api/latest/authenticationtoken";
    String ERROR_MESSAGE_NO_CREDENTIALS = "No username and/or password provided";
    String ERROR_MESSAGE_401 = "Wrong username and/or password";
    String ERROR_MESSAGE_404 = "Service is unavailable";
    String ERROR_MESSAGE_DEFAULT = "Unexpected error";
    String ERROR_MESSAGE_NO_TICKET = "Unable to retrieve login ticket";
}
