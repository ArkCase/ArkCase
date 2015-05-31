package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergey.kolomiets  on 5/26/15.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/admin", "/api/latest/plugin/admin"} )
public class LdapConfigurationUpdateDirectory {
    private Logger log = LoggerFactory.getLogger(getClass());

    private String ldapConfigurationLocation;
    private String ldapPropertiesFile;
    private String ldapTemplatePropertiesFile;
    private String ldapConfigurationTemplatesLocation;
    
    @RequestMapping(value = "/ldapconfiguration/directories/{directoryId}", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public void updateDirectory(
            @RequestBody String resource,
            @PathVariable("directoryId") String directoryId,
            HttpServletResponse response) throws IOException, AcmLdapConfigurationException {

        try {

            JSONObject ldapObject = new JSONObject(resource);
            if (directoryId == null) {
                throw new AcmLdapConfigurationException("Directory Id is undefined");
            }

            String propertiesFileName = ldapConfigurationLocation + String.format(ldapPropertiesFile, directoryId);
            if (!(new File(propertiesFileName)).exists()) {
                throw new AcmLdapConfigurationException(String.format("Can't find property file '%s'", propertiesFileName));
            }

            // Update Properties file
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));
            try {
                Template tmplProperties = cfg.getTemplate(ldapTemplatePropertiesFile);
                Map<String, Object> props = new HashMap<String, Object>();
                props.put(LdapConfigurationProperties.LDAP_PROP_ID, ldapObject.getString(LdapConfigurationProperties.LDAP_PROP_ID));

                props.put("id", ldapObject.getString(LdapConfigurationProperties.LDAP_PROP_ID));
                props.put("name", ldapObject.getString(LdapConfigurationProperties.LDAP_PROP_NAME));
                props.put("base", ldapObject.getString(LdapConfigurationProperties.LDAP_PROP_BASE));
                props.put("directoryName", ldapObject.getString(LdapConfigurationProperties.LDAP_PROP_DIR_NAME));
                props.put("authUserDn", ldapObject.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_DN));
                props.put("authUserPassword", ldapObject.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_PASSWORD));
                props.put("groupSearchBase", ldapObject.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_BASE));
                props.put("ldapUrl", ldapObject.getString(LdapConfigurationProperties.LDAP_PROP_LDAP_URL));
                props.put("userIdAttributeName",ldapObject.getString(LdapConfigurationProperties.LDAP_PROP_USER_ID_ATTR_NAME));


                Writer fileWriter = null;
                try {
                    fileWriter = new FileWriter(new File(propertiesFileName));
                    tmplProperties.process(props, fileWriter);
                } finally {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                }
                response.getOutputStream().write("{}".getBytes());
                response.getOutputStream().flush();
            } catch(Exception e) {
                throw new AcmLdapConfigurationException("Can't updae LDAP properties file ", e);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't update LDAP directory"));
            }
            throw new AcmLdapConfigurationException("Update LDAP directory error", e);

        }
    }

    public void setLdapConfigurationLocation(String ldapConfigurationLocation) {
        this.ldapConfigurationLocation = ldapConfigurationLocation;
    }

    public void setLdapPropertiesFile(String ldapPropertiesFile) {
        this.ldapPropertiesFile = ldapPropertiesFile;
    }

    public void setLdapTemplatePropertiesFile(String ldapTemplatePropertiesFile) {
        this.ldapTemplatePropertiesFile = ldapTemplatePropertiesFile;
    }

    public void setLdapConfigurationTemplatesLocation(String ldapConfigurationTemplatesLocation) {
        this.ldapConfigurationTemplatesLocation = ldapConfigurationTemplatesLocation;
    }


}