package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import freemarker.template.Configuration;
import freemarker.template.Template;


/**
 * Created by sergey.kolomiets  on 5/26/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LdapConfigurationCreateDirectory {
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/ldapconfiguration/directories", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public String createDirectory(
            @RequestBody String resource,
            HttpServletResponse response) throws IOException, AcmLdapConfigurationException {

        try {
            JSONObject newLdapObject = new JSONObject(resource);
            String id = newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_ID);

            if (id == null) {
                throw new AcmLdapConfigurationException("ID is undefined");
            }

            HashMap<String, Object> props = LdapConfigurationService.getProperties(newLdapObject);

            // Create LDAP Direcotry
            LdapConfigurationService.createLdapDirectory(id, props);

            return newLdapObject.toString();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't create LDAP directory", e);
            }
            throw new AcmLdapConfigurationException("Create LDAP directory error", e);
        }
    }
}
