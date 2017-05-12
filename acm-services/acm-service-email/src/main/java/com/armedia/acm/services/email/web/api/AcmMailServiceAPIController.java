package com.armedia.acm.services.email.web.api;

import com.armedia.acm.services.email.service.AcmAddressBookItem;
import com.armedia.acm.services.email.service.AcmEmail;
import com.armedia.acm.services.email.service.AcmEmailException;
import com.armedia.acm.services.email.service.AcmEmailServiceException;
import com.armedia.acm.services.email.service.AcmEmailValidationException;
import com.armedia.acm.services.email.service.AcmMailService;
import com.armedia.acm.services.email.service.MailServiceExceptionMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 27, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/email", "/api/latest/service/email" })
public class AcmMailServiceAPIController
{

    private AcmMailService mailService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmAddressBookItem> listContacts(HttpSession session, Authentication auth,
            @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "maxItems", required = false, defaultValue = "50") int maxItems)
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        return mailService.retrieveAddressBook(user, auth).getContacts(sort, sortDirection, start, maxItems);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> sendEmail(HttpSession session, Authentication auth, @RequestBody AcmEmail email)
            throws AcmEmailValidationException, AcmEmailException
    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");
        mailService.sendEmail(user, auth, email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler({ AcmEmailValidationException.class, AcmEmailException.class })
    @ResponseBody
    public ResponseEntity<?> handleEmailException(AcmEmailServiceException ce)
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
