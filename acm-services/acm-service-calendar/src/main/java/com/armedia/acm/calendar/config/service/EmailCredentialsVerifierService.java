package com.armedia.acm.calendar.config.service;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 12, 2017
 *
 */
public interface EmailCredentialsVerifierService
{

    boolean verifyEmailCredentials(String userId, EmailCredentials emailCredentials);

}
