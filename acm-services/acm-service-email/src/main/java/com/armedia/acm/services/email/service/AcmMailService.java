package com.armedia.acm.services.email.service;

import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     * @return
     */
    SMTPConfiguration getSMTPConfiguration(AcmUser user, Authentication auth);

    /**
     * @param user
     * @param auth
     * @param configuration
     */
    void updateSMTPConfiguration(AcmUser user, Authentication auth, SMTPConfiguration configuration)
            throws AcmSMTPConfigurationValidationException;

    /**
     * @return
     */
    List<EmailTemplateConfiguration> getTemplateConfigurations();

    /**
     * @param templateData
     * @param template
     */
    void updateEmailTemplate(EmailTemplateConfiguration templateData, MultipartFile template);

    /**
     *
     * @param email
     * @param objectType
     * @param source
     * @param actions
     *            TODO
     * @return
     */
    List<EmailTemplateConfiguration> getTemplateCandidates(String email, String objectType, EmailSource source, List<String> actions);

    /**
     *
     * @param templateName
     * @return
     */
    String getTemplate(String templateName);

    /**
     *
     * @param templateName
     */
    void deleteTemplate(String templateName);

    /**
     * @param ce
     * @return
     */
    <ME extends AcmEmailServiceException> MailServiceExceptionMapper<ME> getExceptionMapper(AcmEmailServiceException e);

}
