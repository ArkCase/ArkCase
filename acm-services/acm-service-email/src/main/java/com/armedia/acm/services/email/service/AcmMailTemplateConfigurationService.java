package com.armedia.acm.services.email.service;

import com.armedia.acm.services.email.model.EmailTemplateValidationResponse;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 27, 2017
 *
 */
public interface AcmMailTemplateConfigurationService
{

    /**
     * @return
     * @throws AcmEmailServiceException
     */
    List<EmailTemplateConfiguration> getTemplateConfigurations() throws AcmEmailConfigurationException;

    /**
     * @param templateName
     * @return
     * @throws AcmEmailServiceException
     */
    EmailTemplateValidationResponse validateEmailTemplate(EmailTemplateConfiguration templateData) throws AcmEmailConfigurationException;

    /**
     * @param templateData
     * @param template
     * @throws AcmEmailConfigurationException
     */
    void updateEmailTemplate(EmailTemplateConfiguration templateData, MultipartFile template) throws AcmEmailConfigurationException;

    /**
     *
     * @param email
     * @param objectType
     * @param source
     * @param actions
     * @return
     * @throws AcmEmailConfigurationException
     */
    List<EmailTemplateConfiguration> getMatchingTemplates(String email, String objectType, EmailSource source, List<String> actions)
            throws AcmEmailConfigurationException;

    /**
     *
     * @param templateName
     * @return
     * @throws AcmEmailConfigurationException
     */
    String getTemplate(String templateName) throws AcmEmailConfigurationException;

    /**
     *
     * @param templateName
     * @throws AcmEmailConfigurationException
     */
    void deleteTemplate(String templateName) throws AcmEmailConfigurationException;

    /**
     * @param ce
     * @return
     */
    <ME extends AcmEmailServiceException> AcmEmailServiceExceptionMapper<ME> getExceptionMapper(AcmEmailServiceException e);

}
