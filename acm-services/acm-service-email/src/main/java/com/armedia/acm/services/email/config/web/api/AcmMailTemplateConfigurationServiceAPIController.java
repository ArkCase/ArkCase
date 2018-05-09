package com.armedia.acm.services.email.config.web.api;

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
import com.armedia.acm.services.email.service.AcmEmailConfigurationException;
import com.armedia.acm.services.email.service.AcmEmailServiceException;
import com.armedia.acm.services.email.service.AcmEmailServiceExceptionMapper;
import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.email.service.EmailTemplateConfiguration;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/email/configure/template", "/api/latest/service/email/configure/template" })
public class AcmMailTemplateConfigurationServiceAPIController
{

    private AcmMailTemplateConfigurationService mailService;

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public List<EmailTemplateConfiguration> getTemplateConfigurations() throws AcmEmailConfigurationException
    {
        return mailService.getTemplateConfigurations();
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = { "multipart/mixed", MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateEmailTemplate(@RequestPart("data") EmailTemplateConfiguration templateConfiguration,
            @RequestPart(value = "file", required = false) MultipartFile template) throws AcmEmailConfigurationException
    {
        mailService.updateEmailTemplate(templateConfiguration, template);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/validate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailTemplateValidationResponse validateEmailTemplate(@RequestBody EmailTemplateConfiguration templateConfiguration)
            throws AcmEmailConfigurationException
    {
        return mailService.validateEmailTemplate(templateConfiguration);
    }

    @RequestMapping(path = "/{templateName:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteEmailTemplate(@PathVariable(value = "templateName") String templateName)
            throws AcmEmailConfigurationException
    {
        mailService.deleteTemplate(templateName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(path = "/{templateName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> getEmailTemplate(@PathVariable(value = "templateName") String templateName)
            throws AcmEmailConfigurationException
    {
        return Collections.singletonMap("content", mailService.getTemplate(templateName));
    }

    @ExceptionHandler(AcmEmailConfigurationException.class)
    @ResponseBody
    public ResponseEntity<?> handleConfigurationException(AcmEmailServiceException ce)
    {
        AcmEmailServiceExceptionMapper<AcmEmailServiceException> exceptionMapper = mailService.getExceptionMapper(ce);
        Object errorDetails = exceptionMapper.mapException(ce);
        return ResponseEntity.status(exceptionMapper.getStatusCode()).body(errorDetails);
    }

    /**
     * @param mailService
     *            the mailService to set
     */
    public void setMailService(AcmMailTemplateConfigurationService mailService)
    {
        this.mailService = mailService;
    }

}
