package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.armedia.acm.plugins.admin.model.CmisUrlConfig;
import com.armedia.acm.plugins.admin.service.CmisConfigurationService;
import com.armedia.mule.cmis.basic.auth.HttpInvokerUtil;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/admin/config/url-validation", "/api/latest/plugin/admin/config/url-validation" })
public class CmisConfigurationUrlValidation
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private CmisConfigurationService cmisConfigurationService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity validateUrl(@RequestBody CmisUrlConfig cmsiUrlConfig) throws AcmEncryptionException {
        try
        {
            List<Repository> repositories = cmisConfigurationService.getRepositories(cmsiUrlConfig);
            log.info("Found repository with ID: " + repositories.get(0).getId());
        }
        catch (CmisConnectionException | CmisUnauthorizedException cmisException) {
            HashMap<String, String> urlError = new HashMap<>();
            urlError.put("message", cmisException.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(urlError);
        }
        catch (Exception ex)
        {
            HashMap<String, String> urlError = new HashMap<>();
            urlError.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(urlError);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public CmisConfigurationService getCmisConfigurationService() {
        return cmisConfigurationService;
    }

    public void setCmisConfigurationService(CmisConfigurationService cmisConfigurationService) {
        this.cmisConfigurationService = cmisConfigurationService;
    }

    public class UrlError
    {
        private BigInteger code;
        private String message;

        public BigInteger getCode()
        {
            return code;
        }

        public void setCode(BigInteger code)
        {
            this.code = code;
        }

        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }
    }
}
