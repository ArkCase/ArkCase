package com.armedia.acm.services.signature.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.signature.dao.SignatureDao;
import com.armedia.acm.services.signature.exception.AcmSignatureException;
import com.armedia.acm.services.signature.model.ApplicationSignatureEvent;
import com.armedia.acm.services.signature.model.Signature;
import com.armedia.acm.services.signature.service.SignatureEventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/signature", "/api/latest/plugin/signature" })
public class SignatureAPIController
{
    private SignatureDao signatureDao;
    private SignatureEventPublisher signatureEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/confirm/{objectType}/{objectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Signature signTask(
    		@PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            @RequestParam(value="confirmPassword", required=true) String password,
            Authentication authentication,
            HttpSession httpSession,
            HttpServletResponse response
    ) throws AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Electronically signing object['" + objectType + "][" + objectId + "]");
        }
        
        try
        {       	
        	String user = authentication.getName();
        	
        	// TODO sign task by authenticating against ldap
        	boolean isCorrectPassword = true;
        	
        	Signature signature = new Signature();
        	
        	if (!isCorrectPassword)
        	{
        		throw new AcmSignatureException("Password was incorrect");
        	}
        	// persist to db
        	signature.setObjectId(objectId);
        	signature.setObjectType(objectType);
        	signature.setSignedBy(user);
        	
            Signature savedSignature = getSignatureDao().save(signature);

            publishSignatureEvent(authentication, httpSession, signature, true);

            return signature;
        }
        catch (Exception e)
        {
            // gen up a fake task so we can audit the failure
        	Signature fakeSignature = new Signature();
            fakeSignature.setObjectId(objectId);
            fakeSignature.setObjectType(objectType);
            publishSignatureEvent(authentication, httpSession, fakeSignature, false);

            throw new AcmUserActionFailedException("sign", objectType, objectId, e.getMessage(), e);
        }

    }
    
    protected void publishSignatureEvent(
            Authentication authentication,
            HttpSession httpSession,
            Signature signed,
            boolean succeeded)
    {
        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        ApplicationSignatureEvent event = new ApplicationSignatureEvent(signed, "sign", succeeded, ipAddress);
        getSignatureEventPublisher().publishSignatureEvent(event);
    }

	public SignatureDao getSignatureDao() {
		return signatureDao;
	}

	public void setSignatureDao(SignatureDao signatureDao) {
		this.signatureDao = signatureDao;
	}

	public SignatureEventPublisher getSignatureEventPublisher() {
		return signatureEventPublisher;
	}

	public void setSignatureEventPublisher(
			SignatureEventPublisher signatureEventPublisher) {
		this.signatureEventPublisher = signatureEventPublisher;
	}

}

