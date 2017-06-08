package com.armedia.acm.services.email.config.web.api;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @RequestMapping(path = "/{templateName}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteEmailTemplate(@PathVariable(value = "templateName") String templateName)
            throws AcmEmailConfigurationException
    {
        mailService.deleteTemplate(templateName);
        return new ResponseEntity<>(HttpStatus.OK);
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
