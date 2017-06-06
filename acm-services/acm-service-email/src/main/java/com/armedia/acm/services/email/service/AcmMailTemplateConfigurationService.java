package com.armedia.acm.services.email.service;

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
    <ME extends AcmEmailServiceException> AcmEmailServiceExceptionMapper<ME> getExceptionMapper(
            AcmEmailServiceException e);

}
