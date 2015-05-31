package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
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

    private String ldapConfigurationLocation;
    private String ldapLoginFile;
    private String ldapSignatureFile;
    private String ldapSyncFile;
    private String ldapPropertiesFile;

    private String ldapPropertiesFileRegex;

    @RequestMapping(value = "/ldapconfiguration/directories/{directoryId}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })

    @ResponseBody
    public void createDirectory(
            @RequestBody String resource,
            @PathVariable("directoryId") String directoryId,
            HttpServletResponse response) throws IOException, AcmLdapConfigurationException {

        try {
            if (directoryId == null) {
                throw new AcmLdapConfigurationException("Directory Id is undefined");
            }

            String[] extensions = new String[] {"properties"};
            List<File> propertiesFiles = (List<File>) FileUtils.listFiles(new File(ldapConfigurationLocation), extensions, false);

            Pattern pattern = Pattern.compile(ldapPropertiesFileRegex);
            int matchedFiles = 0;
            for (File fileIter : propertiesFiles) {
                String fileName = fileIter.getName();
                Matcher matcher =  pattern.matcher(fileName);
                if (matcher.find()) {
                    matchedFiles++;
                }
            }

            if (matchedFiles == 0) {
                throw new AcmLdapConfigurationException("There are no LDAP properties files");
            }

            if (matchedFiles == 1) {
                throw new AcmLdapConfigurationException("Can't delete last LDAP file");
            }

            FileUtils.forceDelete(
                new File(ldapConfigurationLocation + String.format(ldapPropertiesFile, directoryId))
            );

            FileUtils.forceDelete(
                new File(ldapConfigurationLocation + String.format(ldapLoginFile, directoryId))
            );

            FileUtils.forceDelete(
                new File(ldapConfigurationLocation + String.format(ldapSignatureFile, directoryId))
            );

            FileUtils.forceDelete(
                new File(ldapConfigurationLocation + String.format(ldapSyncFile, directoryId))
            );
            response.getOutputStream().write("{}".getBytes());
            response.getOutputStream().flush();

        } catch (Exception e) {
            String error = "Can't delete LDAP directory";
            if (log.isErrorEnabled()) {
                log.error(error);
            }
            throw new AcmLdapConfigurationException("Delete LDAP directory error", e);
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

    public void setLdapPropertiesFileRegex(String ldapPropertiesFileRegex) {
        this.ldapPropertiesFileRegex = ldapPropertiesFileRegex;
    }
}
