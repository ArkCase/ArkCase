package com.armedia.acm.services.email.service;

import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.security.core.Authentication;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 27, 2017
 *
 */
public interface AcmMailService
{

    /**
     *
     * @param user
     * @param auth
     * @return
     */
    AcmAddressBook retrieveAddressBook(AcmUser user, Authentication auth);

    /**
     * @param user
     * @param auth
     * @param email
     */
    void sendEmail(AcmUser user, Authentication auth, AcmEmail email) throws AcmEmailValidationException, AcmEmailException;

    /**
     * @param user
     * @param auth
     * @param configuration
     */
    void updateSMTPConfiguration(AcmUser user, Authentication auth, SMTPConfiguration configuration)
            throws AcmSMTPConfigurationValidationException;

    /**
     * @param user
     * @param auth
     * @param template
     */
    void updateEmailTemplate(AcmUser user, Authentication auth, EmailBodyTemplate template);

    /**
     *
     * @param email
     * @param objectType
     * @param source
     * @return
     */
    EmailBodyTemplate getTemplate(String email, String objectType, EmailSource source);

    /**
     * @param ce
     * @return
     */
    <ME extends AcmEmailServiceException> MailServiceExceptionMapper<ME> getExceptionMapper(AcmEmailServiceException e);

}
