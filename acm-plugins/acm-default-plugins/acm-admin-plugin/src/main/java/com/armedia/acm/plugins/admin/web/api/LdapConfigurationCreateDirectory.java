package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;


@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class LdapConfigurationCreateDirectory
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private LdapConfigurationService ldapConfigurationService;

    @RequestMapping(value = "/ldapconfiguration/directories", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String createDirectory(@RequestBody String resource) throws IOException, AcmLdapConfigurationException
    {
        try
        {
            JSONObject newLdapObject = new JSONObject(resource);
            String id = newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_ID);
            String directoryType = newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_DIRECTORY_TYPE);

            if (id == null)
            {
                throw new AcmLdapConfigurationException("ID is undefined");
            }

            HashMap<String, Object> props = ldapConfigurationService.getProperties(newLdapObject);
            ldapConfigurationService.createLdapDirectoryConfigurations(id, directoryType, props);
            return newLdapObject.toString();
        } catch (Exception e)
        {
            log.error("Can't create LDAP directory", e);
            throw new AcmLdapConfigurationException("Create LDAP directory error", e);
        }
    }

    public void setLdapConfigurationService(LdapConfigurationService ldapConfigurationService)
    {
        this.ldapConfigurationService = ldapConfigurationService;
    }
}
