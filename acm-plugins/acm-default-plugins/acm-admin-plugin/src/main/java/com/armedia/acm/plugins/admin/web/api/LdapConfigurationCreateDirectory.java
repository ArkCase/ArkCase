package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String ldapConfigurationLocation;
    private String ldapLoginFile;
    private String ldapSignatureFile;
    private String ldapSyncFile;
    private String ldapPropertiesFile;

    private String ldapConfigurationTemplatesLocation;
    private String ldapTemplateLoginFile;
    private String ldapTemplateSignatureFile;
    private String ldapTemplateSyncFile;
    private String ldapTemplatePropertiesFile;


    @RequestMapping(value = "/ldapconfiguration/directories", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public void createDirectory(
            @RequestBody String resource,
            HttpServletResponse response) throws IOException, AcmLdapConfigurationException {

        try {
            JSONObject newLdapObject = new JSONObject(resource);
            String id = newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_ID);

            if (id == null) {
                throw new AcmLdapConfigurationException("ID is undefined");
            }

            // Validate ID. it can contain symbols and numbers only


            // Be sure that ID is uniq
            String propertiesFileName = ldapConfigurationLocation + String.format(ldapPropertiesFile, id);
            if (new File(propertiesFileName).exists()){
                throw new AcmLdapConfigurationException(String.format("Directory with ID='%s' is present in the system.", id));
            }

            // Create Properties file
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));
            Map<String, Object> props = null;
            try {
                Template tmplProperties = cfg.getTemplate(ldapTemplatePropertiesFile);
                props = new HashMap<String, Object>();
                props.put(LdapConfigurationProperties.LDAP_PROP_ID, newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_ID));

                props.put("id", newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_ID));
                props.put("name", newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_NAME));
                props.put("base", newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_BASE));
                props.put("directoryName", newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_DIR_NAME));
                props.put("authUserDn", newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_DN));
                props.put("authUserPassword", newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_PASSWORD));
                props.put("groupSearchBase", newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_BASE));
                props.put("ldapUrl", newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_LDAP_URL));
                props.put("userIdAttributeName",newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_USER_ID_ATTR_NAME));
                props.put("propertiesFileName", propertiesFileName);

                Writer writerProp = null;
                try {
                    writerProp = new FileWriter(new File(propertiesFileName));
                    tmplProperties.process(props, writerProp);
                } finally {
                    if (writerProp != null) {
                        writerProp.close();
                    }
                }

            } catch(Exception e) {
                throw new AcmLdapConfigurationException("Can't create LDAP properties file ", e);
            }

            // Create xml files
            try {
                // Login file
                Template tmplLogin = cfg.getTemplate(ldapTemplateLoginFile);
                Writer writerLogin= null;
                try {
                    writerLogin = new FileWriter(new File(ldapConfigurationLocation + String.format(ldapLoginFile, id)));
                    tmplLogin.process(props, writerLogin);
                } finally {
                    if (writerLogin != null) {
                        writerLogin.close();
                    }
                }

                // Signature file
                Template tmplSig = cfg.getTemplate(ldapTemplateSignatureFile);
                Writer writerSig = null;
                try {
                    writerSig = new FileWriter(new File(ldapConfigurationLocation + String.format(ldapSignatureFile, id)));
                    tmplSig.process(props, writerSig);
                } finally {
                    if (writerSig != null) {
                        writerSig.close();
                    }
                }

                // Sync file
                Template tmplSync = cfg.getTemplate(ldapTemplateSyncFile);
                Writer writerSync = null;
                try {
                    writerSync = new FileWriter(new File(ldapConfigurationLocation + String.format(ldapSyncFile, id)));
                    tmplSync.process(props, writerSync);
                } finally {
                    if (writerSync != null) {
                        writerSync.close();
                    }
                }

            } catch(Exception e) {
                throw  new AcmLdapConfigurationException("Can't create LDAP XML files", e) ;
            }

            JSONObject returnObject = new JSONObject();
            returnObject.put(LdapConfigurationProperties.LDAP_PROP_ID, newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_ID));
            returnObject.put(LdapConfigurationProperties.LDAP_PROP_NAME, newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_NAME));
            returnObject.put(LdapConfigurationProperties.LDAP_PROP_BASE, newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_BASE));
            returnObject.put(LdapConfigurationProperties.LDAP_PROP_DIR_NAME, newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_DIR_NAME));
            returnObject.put(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_DN, newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_DN));
            returnObject.put(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_PASSWORD, newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_PASSWORD));
            returnObject.put(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_BASE, newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_BASE));
            returnObject.put(LdapConfigurationProperties.LDAP_PROP_LDAP_URL, newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_LDAP_URL));
            returnObject.put(LdapConfigurationProperties.LDAP_PROP_USER_ID_ATTR_NAME,newLdapObject.getString(LdapConfigurationProperties.LDAP_PROP_USER_ID_ATTR_NAME));

            response.getOutputStream().write(returnObject.toString().getBytes());
            response.getOutputStream().flush();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't create LDAP directory"));
            }
            throw new AcmLdapConfigurationException("Create LDAP directory error", e);
        }
    }

    public void setLdapConfigurationLocation(String ldapConfigurationLocation) {
        this.ldapConfigurationLocation = ldapConfigurationLocation;
    }

    public void setLdapLoginFile(String ldapLoginFile) {
        this.ldapLoginFile = ldapLoginFile;
    }

    public void setLdapSignatureFile(String ldapSignatureFile) {
        this.ldapSignatureFile = ldapSignatureFile;
    }

    public void setLdapSyncFile(String ldapSyncFile) {
        this.ldapSyncFile = ldapSyncFile;
    }

    public void setLdapPropertiesFile(String ldapPropertiesFile) {
        this.ldapPropertiesFile = ldapPropertiesFile;
    }

    public void setLdapConfigurationTemplatesLocation(String ldapConfigurationTemplatesLocation) {
        this.ldapConfigurationTemplatesLocation = ldapConfigurationTemplatesLocation;
    }

    public void setLdapTemplateLoginFile(String ldapTemplateLoginFile) {
        this.ldapTemplateLoginFile = ldapTemplateLoginFile;
    }

    public void setLdapTemplateSignatureFile(String ldapTemplateSignatureFile) {
        this.ldapTemplateSignatureFile = ldapTemplateSignatureFile;
    }

    public void setLdapTemplateSyncFile(String ldapTemplateSyncFile) {
        this.ldapTemplateSyncFile = ldapTemplateSyncFile;
    }

    public void setLdapTemplatePropertiesFile(String ldapTemplatePropertiesFile) {
        this.ldapTemplatePropertiesFile = ldapTemplatePropertiesFile;
    }
}
