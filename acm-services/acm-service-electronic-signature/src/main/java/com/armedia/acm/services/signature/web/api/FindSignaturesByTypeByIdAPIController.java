package com.armedia.acm.services.signature.web.api;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.signature.dao.SignatureDao;
import com.armedia.acm.services.signature.model.Signature;

@RequestMapping({ "/api/v1/plugin/signature", "/api/latest/plugin/signature" })
public class FindSignaturesByTypeByIdAPIController {
	
	private SignatureDao signatureDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/find/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Signature> findSignaturesByTypeById(
    		@PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
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

	public SignatureDao getSignatureDao() {
		return signatureDao;
	}

	public void setSignatureDao(SignatureDao signatureDao) {
		this.signatureDao = signatureDao;
	}
}
