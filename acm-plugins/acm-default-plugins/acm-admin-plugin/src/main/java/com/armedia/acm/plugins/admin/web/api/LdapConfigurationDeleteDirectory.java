package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
            @RequestBody String resource,
            @PathVariable("directoryId") String directoryId,
            HttpServletResponse response) throws IOException, AcmLdapConfigurationException {

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
