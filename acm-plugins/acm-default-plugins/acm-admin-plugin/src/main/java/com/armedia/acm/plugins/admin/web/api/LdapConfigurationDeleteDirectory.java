package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
/**
 * Created by sergey.kolomiets  on 5/26/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LdapConfigurationDeleteDirectory {
    private Logger log = LoggerFactory.getLogger(getClass());

    private LdapConfigurationService ldapConfigurationService;

    @RequestMapping(value = "/ldapconfiguration/directories/{directoryId}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public String deleteDirectory(
            @PathVariable("directoryId") String directoryId) throws IOException, AcmLdapConfigurationException {

        try {
            if (directoryId == null) {
                throw new AcmLdapConfigurationException("Directory Id is undefined");
            }
            ldapConfigurationService.deleteLdapDirectory(directoryId);
            return "{}";

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't delete LDAP directory", e);
            }
            throw new AcmLdapConfigurationException("Delete LDAP directory error", e);
        }
    }

    public void setLdapConfigurationService(LdapConfigurationService ldapConfigurationService) {
        this.ldapConfigurationService = ldapConfigurationService;
    }
}
