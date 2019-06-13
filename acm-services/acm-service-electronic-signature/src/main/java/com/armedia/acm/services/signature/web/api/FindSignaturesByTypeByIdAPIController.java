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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.signature.dao.SignatureDao;
import com.armedia.acm.services.signature.model.Signature;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

@RequestMapping({ "/api/v1/plugin/signature", "/api/latest/plugin/signature" })
public class FindSignaturesByTypeByIdAPIController
{

    private SignatureDao signatureDao;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/find/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Signature> findSignaturesByTypeById(
            @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            Authentication authentication,
            HttpSession httpSession) throws AcmUserActionFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Find signatures for object['" + objectType + "][" + objectId + "]");
        }

        try
        {
            List<Signature> signatureList = getSignatureDao().findByObjectIdObjectType(objectId, objectType);

            return signatureList;
        }
        catch (Exception e)
        {
            throw new AcmUserActionFailedException("find", objectType, objectId, e.getMessage(), e);
        }
    }

    public SignatureDao getSignatureDao()
    {
        return signatureDao;
    }

    public void setSignatureDao(SignatureDao signatureDao)
    {
        this.signatureDao = signatureDao;
    }
}
