package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;


@Controller
@RequestMapping({"/api/v1/plugin/admin/ldapconfiguration", "/api/latest/plugin/admin/ldapconfiguration"})
public class LdapTemplateConfigurationAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private LdapConfigurationService ldapConfigurationService;

    @RequestMapping(value = "/openLdapUserTemplate/{templateId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> createOpenLdapUserTemplates(@RequestBody Map<String, String> templateProperties,
                                                           @PathVariable String templateId)
            throws IOException, AcmLdapConfigurationException
    {
        try
        {
            return ldapConfigurationService.createOpenLdapUserTemplateFiles(templateId, templateProperties);
        } catch (Exception e)
        {
            log.error("Can't create Open Ldap configuration for adding user", e);
            throw new AcmLdapConfigurationException("Can't create Open Ldap configuration for adding user", e);
        }
    }

    @RequestMapping(value = "/adUserTemplate/{templateId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> createActiveDirectoryUserTemplates(@RequestBody Map<String, String> templateProperties,
                                                                  @PathVariable String templateId)
            throws IOException, AcmLdapConfigurationException
    {
        try
        {
            return ldapConfigurationService.createActiveDirectoryUserTemplateFiles(templateId, templateProperties);
        } catch (Exception e)
        {
            log.error("Can't create Active Directory configuration for adding user", e);
            throw new AcmLdapConfigurationException("Can't create Active Directory configuration for adding user", e);
        }
    }

    public LdapConfigurationService getLdapConfigurationService()
    {
        return ldapConfigurationService;
    }

    public void setLdapConfigurationService(LdapConfigurationService ldapConfigurationService)
    {
        this.ldapConfigurationService = ldapConfigurationService;
    }
}
