package com.armedia.acm.services.email.config.web.api;

import com.armedia.acm.services.email.service.AcmEmailServiceException;
import com.armedia.acm.services.email.service.AcmMailService;
import com.armedia.acm.services.email.service.AcmSMTPConfigurationValidationException;
import com.armedia.acm.services.email.service.EmailBodyTemplate;
import com.armedia.acm.services.email.service.MailServiceExceptionMapper;
import com.armedia.acm.services.email.service.SMTPConfiguration;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 28, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/email/configure", "/api/latest/service/email/configure" })
public class AcmMailConfigurationServiceAPIController
{

    private AcmMailService mailService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SMTPConfiguration getSMTPConfiguration(HttpSession session, Authentication auth)
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        return mailService.getSMTPConfiguration(user, auth);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> updateSMTPConfiguration(HttpSession session, Authentication auth, @RequestBody SMTPConfiguration configuration)
            throws AcmSMTPConfigurationValidationException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        mailService.updateSMTPConfiguration(user, auth, configuration);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> updateEmailTemplate(HttpSession session, Authentication auth, @RequestBody EmailBodyTemplate template)
            throws AcmSMTPConfigurationValidationException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        mailService.updateEmailTemplate(user, auth, template);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(AcmSMTPConfigurationValidationException.class)
    @ResponseBody
    public ResponseEntity<?> handleConfigurationException(AcmEmailServiceException ce)
    {
        MailServiceExceptionMapper<AcmEmailServiceException> exceptionMapper = mailService.getExceptionMapper(ce);
        Object errorDetails = exceptionMapper.mapException(ce);
        return ResponseEntity.status(exceptionMapper.getStatusCode()).body(errorDetails);
    }

    /**
     * @param mailService
     *            the mailService to set
     */
    public void setMailService(AcmMailService mailService)
    {
        this.mailService = mailService;
    }

}
