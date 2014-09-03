package com.armedia.acm.services.signature.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.signature.dao.SignatureDao;
import com.armedia.acm.services.signature.exception.AcmSignatureException;
import com.armedia.acm.services.signature.model.ApplicationSignatureEvent;
import com.armedia.acm.services.signature.model.Signature;
import com.armedia.acm.services.signature.service.SignatureEventPublisher;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateManager;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/signature", "/api/latest/plugin/signature" })
public class SignatureAPIController
{
    private SignatureDao signatureDao;
    private SignatureEventPublisher signatureEventPublisher;
    private LdapAuthenticateManager ldapAuthenticateManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/confirm/{objectType}/{objectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Signature signObject(
    		@PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            @RequestParam(value="confirmPassword", required=true) String password,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Electronically signing object['" + objectType + "][" + objectId + "]");
        }
        
        try
        {       	
        	if (StringUtils.isBlank(password)) 
        	{
        		throw new AcmSignatureException("Password blank");
        	}
        	
        	String userName = authentication.getName();
        	
        	// authenticate user/password against ldap service(s)
        	Boolean isAuthenticated = getLdapAuthenticateManager().authenticate(userName, password);
        	if (!isAuthenticated)
        	{
        		throw new AcmSignatureException("Could not authenticate with the password provided");
        	}
        	
        	// persist to db
        	Signature signature = new Signature();
        	signature.setObjectId(objectId);
        	signature.setObjectType(objectType);
        	signature.setSignedBy(userName);
        	
            Signature savedSignature = getSignatureDao().save(signature);

            publishSignatureEvent(authentication, httpSession, savedSignature, true);

            return savedSignature;
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

	public LdapAuthenticateManager getLdapAuthenticateManager() {
		return ldapAuthenticateManager;
	}

	public void setLdapAuthenticateManager(
			LdapAuthenticateManager ldapAuthenticateManager) {
		this.ldapAuthenticateManager = ldapAuthenticateManager;
	}
	
}

