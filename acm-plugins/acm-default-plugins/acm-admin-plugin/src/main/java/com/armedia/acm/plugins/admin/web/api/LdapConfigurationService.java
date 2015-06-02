package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by sergey.kolomiets on 6/2/15.
 */
public class LdapConfigurationService {
    private static Logger log = LoggerFactory.getLogger(LdapConfigurationService.class);

    private static String ldapConfigurationLocation;
    private static String ldapLoginFile;
    private static String ldapSignatureFile;
    private static String ldapSyncFile;
    private static String ldapPropertiesFile;

    private static String ldapConfigurationTemplatesLocation;
    private static String ldapTemplateLoginFile;
    private static String ldapTemplateSignatureFile;
    private static String ldapTemplateSyncFile;
    private static String ldapTemplatePropertiesFile;

    private static String ldapPropertiesFileRegex;


    /**
     * Create LDAP Directory config files
     * @param dirId Directory identifier
     * @param props Directory properties data
     * @throws AcmLdapConfigurationException
     */
    public static void createLdapDirectory(String dirId, Map<String, Object> props) throws AcmLdapConfigurationException {

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher matcher =  pattern.matcher(dirId);
        if (!matcher.find()) {
            throw new AcmLdapConfigurationException("ID has wrong format. Only numbers and characters symbols are allowed");
        }

        // Check if LDAP files are exist
        if (propertiesFileExist(dirId) || syncFileExist(dirId) || signatureFileExist(dirId) || loginFileExist(dirId)){
            throw new AcmLdapConfigurationException(String.format("LDAP directory with ID='%s' exists", dirId));
        }

        try {
            createPropertiesFile(dirId, props);
            createSignatureFile(dirId, props);
            createLoginFile(dirId, props);
            createSyncFile(dirId, props);
        } catch (Exception e) {
            // Delete created files quietly
            deletePropertiesFileQuietly(dirId);
            deleteSignatureFileQuietly(dirId);
            deleteLoginFileQuietly(dirId);
            deleteSyncFileQuietly(dirId);

            if (log.isErrorEnabled()) {
                log.error(String.format("Can't create LDAP directory '%s'", dirId), e);
            }
            throw new AcmLdapConfigurationException(String.format("Can't create LDAP directory with ID='%s'", dirId), e);
        }
    }


    /**
     * UPdate LDAP Directory settings
     * @param dirId
     * @param props
     */
    public static void updateLdapDirectory(String dirId, Map<String, Object> props) throws AcmLdapConfigurationException {
        String propertiesFileName = getPropertiesFileName(dirId);

        if (!propertiesFileExist(dirId)) {
            throw new AcmLdapConfigurationException(String.format("Can't find property file '%s'", propertiesFileName));
        }

        try {
            // Update Properties file
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));
            try {
                Template tmplProperties = cfg.getTemplate(ldapTemplatePropertiesFile);
                Writer fileWriter = null;
                try {
                    fileWriter = new FileWriter(new File(propertiesFileName));
                    tmplProperties.process(props, fileWriter);
                } finally {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                }
            } catch(Exception e) {
                throw new AcmLdapConfigurationException("Can't updae LDAP properties file ", e);
            }

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Can't update LDAP directory settings", e);
            }
            throw new AcmLdapConfigurationException("Can't updae LDAP directory settings ", e);
        }
    }

    /**
     * Delete LDAP Direcory
     * @param dirId
     * @throws AcmLdapConfigurationException
     */
    public static void deleteLdapDirectory(String dirId) throws AcmLdapConfigurationException {

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


        // Delete LDAP config files. If something goes wrong then go ahead add information to the log only

        try {
            FileUtils.forceDelete(
                new File(getPropertiesFileName(dirId))
            );
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't delete file %s ", getPropertiesFileName(dirId)), e);
            }
        }

        try {
            FileUtils.forceDelete(
                new File(getLoginFileName(dirId))
            );
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't delete file %s ", getLoginFileName(dirId)), e);
            }
        }

        try {
            FileUtils.forceDelete(
                new File(getSignatureFileName(dirId))
            );
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't delete file %s ", getSignatureFileName(dirId)), e);
            }
        }

        try {
            FileUtils.forceDelete(
                new File(getSyncFileName(dirId))
            );
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't delete file %s ", getSyncFileName(dirId)), e);
            }
        }

    }


    /**
     * Create Properties file
     * @param dirId Directory identifier
     * @param props Directory properties data
     * @throws AcmLdapConfigurationException
     * @throws IOException
     */
    private static void createPropertiesFile(String dirId, Map<String, Object> props) throws AcmLdapConfigurationException, IOException {
        if (propertiesFileExist(dirId)){
            throw new AcmLdapConfigurationException(String.format("Properties file with ID='%s' is present in the system.", dirId));
        }

        String propertiesFileName = getPropertiesFileName(dirId);

        // Create Properties file
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));

        try {
            Template tmplProperties = cfg.getTemplate(ldapTemplatePropertiesFile);

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
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't create LDAP properties file with ID '%s'",dirId), e);
            }
            throw new AcmLdapConfigurationException("Can't create LDAP properties file ", e);
        }

    }

    /**
     * Create Signature file
     * @param dirId Directory identifier
     * @param props Directory properties data
     * @throws IOException
     * @throws AcmLdapConfigurationException
     */
    private static void createSignatureFile(String dirId, Map<String, Object> props) throws IOException, AcmLdapConfigurationException {
        String signatureFileName = getSignatureFileName(dirId);
        if (signatureFileExist(dirId)){
            throw new AcmLdapConfigurationException(String.format("Signature file '%s' is present in the system.", signatureFileName));
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));

        try {
            // Signature file
            Template tmplSig = cfg.getTemplate(ldapTemplateSignatureFile);
            Writer writerSig = null;
            try {
                writerSig = new FileWriter(new File(signatureFileName));
                tmplSig.process(props, writerSig);
            } finally {
                if (writerSig != null) {
                    writerSig.close();
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't create LDAP signature file with ID '%s'",dirId), e);
            }
            throw new AcmLdapConfigurationException("Can't create LDAP signature file ", e);
        }
    }

    /**
     * Create Sync file
     * @param dirId Directory identifier
     * @param props Directory properties data
     * @throws IOException
     * @throws AcmLdapConfigurationException
     */
    private static void createSyncFile(String dirId, Map<String, Object> props) throws IOException, AcmLdapConfigurationException {
        String syncFileName = getSignatureFileName(dirId);
        if (syncFileExist(dirId)){
            throw new AcmLdapConfigurationException(String.format("Sync file '%s' is present in the system.", syncFileName));
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));

        try {
            Template tmplSync = cfg.getTemplate(ldapTemplateSyncFile);
            Writer writerSync = null;
            try {
                writerSync = new FileWriter(new File(syncFileName));
                tmplSync.process(props, writerSync);
            } finally {
                if (writerSync != null) {
                    writerSync.close();
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't create LDAP sync file with ID '%s'", dirId), e);
            }

            throw new AcmLdapConfigurationException("Can't create LDAP sync file ", e);
        }
    }

    /**
     * Create Login file
     * @param dirId Directory identifier
     * @param props Directory properties data
     * @throws IOException
     * @throws AcmLdapConfigurationException
     */
    private static void createLoginFile(String dirId, Map<String, Object> props) throws IOException, AcmLdapConfigurationException {
        String loginFileName = getLoginFileName(dirId);
        if (loginFileExist(dirId)){
            throw new AcmLdapConfigurationException(String.format("Login file '%s' is present in the system.", loginFileName));
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));
        try {
            Template tmplLogin = cfg.getTemplate(ldapTemplateLoginFile);
            Writer writerLogin= null;
            try {
                writerLogin = new FileWriter(new File(loginFileName));
                tmplLogin.process(props, writerLogin);
            } finally {
                if (writerLogin != null) {
                    writerLogin.close();
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(String.format("Can't create LDAP login file with ID '%s'", dirId), e);
            }
            throw new AcmLdapConfigurationException("Can't create LDAP login file ", e);
        }
    }

    /**
     * Convert LDAP JSON Object to properties map
     * @param jsonObj
     * @return
     */
    public static HashMap<String, Object>getProperties(JSONObject jsonObj) {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put(LdapConfigurationProperties.LDAP_PROP_ID, jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ID));
        props.put("id", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ID));
        props.put("name", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_NAME));
        props.put("base", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_BASE));
        props.put("directoryName", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_DIR_NAME));
        props.put("authUserDn", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_DN));
        props.put("authUserPassword", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_PASSWORD));
        props.put("groupSearchBase", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_BASE));
        props.put("ldapUrl", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_LDAP_URL));
        props.put("userIdAttributeName",jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_ID_ATTR_NAME));
        props.put("propertiesFileName", LdapConfigurationService.getPropertiesFileName(jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ID)));
        return props;
    }

    public static List<File> getPropertiesFiles() {
        // Get All properties files
        String[] extensions = new String[] {"properties"};
        List<File> files = (List<File>) FileUtils.listFiles(new File(ldapConfigurationLocation), extensions, false);
        List<File> propertiesFiles = new ArrayList<File>();

        // Get all properties files that match to ldapPropertiesFileRegex
        Pattern pattern = Pattern.compile(ldapPropertiesFileRegex);
        int matchedFiles = 0;
        for (File fileIter : files) {
            String fileName = fileIter.getName();
            Matcher matcher =  pattern.matcher(fileName);
            if (matcher.find()) {
                propertiesFiles.add(fileIter);
            }
        }

        return propertiesFiles;
    }

    private static void deletePropertiesFileQuietly(String dirId) {
        FileUtils.deleteQuietly(new File(getPropertiesFileName(dirId)));
    }

    private static void deleteSignatureFileQuietly(String dirId) {
        FileUtils.deleteQuietly(new File(getSignatureFileName(dirId)));
    }

    private static void deleteLoginFileQuietly(String dirId) {
        FileUtils.deleteQuietly(new File(getLoginFileName(dirId)));
    }

    private static void deleteSyncFileQuietly(String dirId){
        FileUtils.deleteQuietly(new File(getSyncFileName(dirId)));
    }

    public static String getPropertiesFileName(String dirId)  {
        return ldapConfigurationLocation + String.format(ldapPropertiesFile, dirId);
    }

    public static String getSignatureFileName(String dirId)  {
        return ldapConfigurationLocation + String.format(ldapSignatureFile, dirId);
    }

    public static String getLoginFileName(String dirId)  {
        return ldapConfigurationLocation + String.format(ldapLoginFile, dirId);
    }

    public static String getSyncFileName(String dirId)  {
        return ldapConfigurationLocation + String.format(ldapSyncFile, dirId);
    }

    public static boolean propertiesFileExist(String dirId) {
        String fileName = getPropertiesFileName(dirId);
        return new File(fileName).exists();
    }

    public static boolean signatureFileExist(String dirId) {
        String fileName = getSignatureFileName(dirId);
        return new File(fileName).exists();
    }

    public static boolean loginFileExist(String dirId) {
        String fileName = getLoginFileName(dirId);
        return new File(fileName).exists();
    }

    public static boolean syncFileExist(String dirId) {
        String fileName = getSyncFileName(dirId);
        return new File(fileName).exists();
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

    public void setLdapPropertiesFileRegex(String ldapPropertiesFileRegex) {
        this.ldapPropertiesFileRegex = ldapPropertiesFileRegex;
    }

}
