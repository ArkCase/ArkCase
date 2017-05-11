package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class LdapConfigurationDeleteDirectory
{
    private LdapConfigurationService ldapConfigurationService;

    @RequestMapping(value = "/ldapconfiguration/directories/{directoryId}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public String deleteDirectory(
            @PathVariable("directoryId") String directoryId) throws AcmLdapConfigurationException
    {
            if (directoryId == null)
            {
                throw new AcmLdapConfigurationException("Directory Id is undefined");
            }
            ldapConfigurationService.deleteLdapDirectoryConfigurationFiles(directoryId);
            return "{}";
    }

    public void setLdapConfigurationService(LdapConfigurationService ldapConfigurationService)
    {
        this.ldapConfigurationService = ldapConfigurationService;
    }
}
