package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sergey.kolomiets  on 5/26/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LdapConfigurationRetrieveDirectories {
    private Logger log = LoggerFactory.getLogger(getClass());

    private LdapConfigurationService ldapConfigurationService;


    @RequestMapping(value = "/ldapconfiguration/directories", method = RequestMethod.GET, produces = {
          MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public String retrieveDirectories(
            HttpServletResponse response) throws IOException, AcmLdapConfigurationException {

        try {

            List<File> propertiesFiles = ldapConfigurationService.getPropertiesFiles();

            JSONArray dirsJsonArr = new JSONArray();
            for (File propertyFile: propertiesFiles) {
                Properties prop = new Properties();
                prop.load(FileUtils.openInputStream(propertyFile));

                // Put all properties into JSON Object
                JSONObject dirJsonObj = new JSONObject();
                for (String proName: prop.stringPropertyNames()) {
                    dirJsonObj.put(proName, prop.getProperty(proName));
                }

                dirsJsonArr.put(dirJsonObj);
            }

            return dirsJsonArr.toString();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't read LDAP properties file", e);
            }
            throw new AcmLdapConfigurationException("Can't get LDAP properties", e);
        }
    }

    public void setLdapConfigurationService(LdapConfigurationService ldapConfigurationService) {
        this.ldapConfigurationService = ldapConfigurationService;
    }
}
