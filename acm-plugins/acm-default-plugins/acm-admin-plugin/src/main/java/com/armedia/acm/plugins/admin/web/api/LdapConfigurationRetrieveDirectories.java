package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class LdapConfigurationRetrieveDirectories
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private LdapConfigurationService ldapConfigurationService;


    @RequestMapping(value = "/ldapconfiguration/directories", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveDirectories() throws AcmLdapConfigurationException
    {
        try
        {
            return ldapConfigurationService.retrieveDirectoriesConfiguration();
        } catch (Exception e)
        {
            log.error("Can't read LDAP properties file", e);
            throw new AcmLdapConfigurationException("Can't get LDAP properties", e);
        }
    }

    public void setLdapConfigurationService(LdapConfigurationService ldapConfigurationService)
    {
        this.ldapConfigurationService = ldapConfigurationService;
    }
}
