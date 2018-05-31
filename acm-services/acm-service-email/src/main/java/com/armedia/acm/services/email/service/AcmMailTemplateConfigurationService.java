package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
