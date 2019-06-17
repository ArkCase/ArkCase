package com.armedia.acm.services.signature.web.api;

/*-
 * #%L
 * ACM Service: Electronic Signature
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

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.signature.dao.SignatureDao;
import com.armedia.acm.services.signature.model.ApplicationSignatureEvent;
import com.armedia.acm.services.signature.model.Signature;
import com.armedia.acm.services.signature.service.SignatureEventPublisher;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateManager;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Map;

@RequestMapping({ "/api/v1/plugin/signature", "/api/latest/plugin/signature" })
public class SignatureAPIController
{
    private SignatureDao signatureDao;
    private SignatureEventPublisher signatureEventPublisher;
    private LdapAuthenticateManager ldapAuthenticateManager;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/confirm/{objectType}/{objectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Signature signObject(
            @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            @RequestBody Map<String, String> body,
            Authentication authentication,
            HttpSession httpSession) throws AcmUserActionFailedException, AcmAppErrorJsonMsg
    {
        String password = body.get("confirmPassword");
        log.info("Electronically signing object [{}] [{}] ", objectType, objectId);

        String userName = authentication.getName();
        // authenticate user/password against ldap service(s)
        Boolean isAuthenticated = getLdapAuthenticateManager().authenticate(userName, password);
        if (!isAuthenticated)
        {
            throw new AcmAppErrorJsonMsg("Invalid password", objectType, "password", null);
        }

        try
        {
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
        ApplicationSignatureEvent event = new ApplicationSignatureEvent(signed,
                String.format("%s.%s", "com.armedia.acm.app.signature.signed", signed.getObjectType().toLowerCase()), succeeded, ipAddress);
        log.debug("Sign event type: [{}]", event.getEventType());
        getSignatureEventPublisher().publishSignatureEvent(event);
    }

    public SignatureDao getSignatureDao()
    {
        return signatureDao;
    }

    public void setSignatureDao(SignatureDao signatureDao)
    {
        this.signatureDao = signatureDao;
    }

    public SignatureEventPublisher getSignatureEventPublisher()
    {
        return signatureEventPublisher;
    }

    public void setSignatureEventPublisher(
            SignatureEventPublisher signatureEventPublisher)
    {
        this.signatureEventPublisher = signatureEventPublisher;
    }

    public LdapAuthenticateManager getLdapAuthenticateManager()
    {
        return ldapAuthenticateManager;
    }

    public void setLdapAuthenticateManager(
            LdapAuthenticateManager ldapAuthenticateManager)
    {
        this.ldapAuthenticateManager = ldapAuthenticateManager;
    }

}
