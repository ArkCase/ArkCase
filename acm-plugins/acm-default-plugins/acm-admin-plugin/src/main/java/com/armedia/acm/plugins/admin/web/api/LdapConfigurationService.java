package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
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
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by sergey.kolomiets on 6/2/15.
 */
public class LdapConfigurationService
{
    private Logger log = LoggerFactory.getLogger(LdapConfigurationService.class);

    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    private String ldapConfigurationLocation;
    private String ldapFile;
    private String ldapPropertiesFile;

    private String ldapConfigurationTemplatesLocation;
    private String ldapTemplateFile;
    private String ldapTemplatePropertiesFile;
    private String ldapPropertiesFileRegex;

    private Properties ldapUserPropertiesFile;

    private String openLdapUserPropertiesTemplate;
    private String openLdapUserFileTemplate;
    private String openLdapUserPropertiesTemplateName;
    private String openLdapUserFileTemplateName;

    private String activeDirectoryUserPropertiesTemplate;
    private String activeDirectoryUserFileTemplate;
    private String activeDirectoryUserPropertiesTemplateName;
    private String activeDirectoryUserFileTemplateName;

    /**
     * Create LDAP Directory config files
     *
     * @param dirId Directory identifier
     * @param props Directory properties data
     * @throws AcmLdapConfigurationException
     */
    public void createLdapDirectory(String dirId, Map<String, Object> props) throws AcmLdapConfigurationException
    {

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9.]+$");
        Matcher matcher = pattern.matcher(dirId);
        if (!matcher.find())
        {
            throw new AcmLdapConfigurationException("ID has wrong format. Only numbers, characters symbols and '.' are allowed");
        }

        // Check if LDAP files exist
        if (propertiesFileExist(dirId) || ldapFileExist(dirId))
        {
            throw new AcmLdapConfigurationException(String.format("LDAP directory with ID='%s' exists", dirId));
        }

        try
        {
            createPropertiesFile(dirId, props);
            createLdapFile(dirId, props);
        } catch (Exception e)
        {
            // Delete created files quietly
            deletePropertiesFileQuietly(dirId);
            deleteLdapFileQuietly(dirId);

            log.error("Can't create LDAP directory '{}' ", dirId, e);
            throw new AcmLdapConfigurationException(String.format("Can't create LDAP directory with ID='%s'", dirId), e);
        }
    }

    /**
     * Update LDAP Directory settings
     *
     * @param dirId Directory identifier
     * @param props Directory properties data
     */
    public void updateLdapDirectory(String dirId, Map<String, Object> props) throws AcmLdapConfigurationException
    {
        String propertiesFileName = getPropertiesFileName(dirId);

        if (!propertiesFileExist(dirId))
        {
            throw new AcmLdapConfigurationException(String.format("Can't find property file '%s'", propertiesFileName));
        }

        writePropertiesFile(dirId, props);
    }

    /**
     * Delete LDAP Direcory
     *
     * @param dirId Directory identifier
     * @throws AcmLdapConfigurationException
     */
    public void deleteLdapDirectory(String dirId) throws AcmLdapConfigurationException
    {

        String[] extensions = new String[]{"properties"};
        List<File> propertiesFiles = (List<File>) FileUtils.listFiles(new File(ldapConfigurationLocation), extensions, false);

        Pattern pattern = Pattern.compile(ldapPropertiesFileRegex);
        int matchedFiles = 0;
        for (File fileIter : propertiesFiles)
        {
            String fileName = fileIter.getName();
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find())
            {
                matchedFiles++;
            }
        }

        if (matchedFiles == 0)
        {
            throw new AcmLdapConfigurationException("There are no LDAP properties files");
        }

        if (matchedFiles == 1)
        {
            throw new AcmLdapConfigurationException("Can't delete last LDAP file");
        }

        // Delete LDAP config files. If something goes wrong then go ahead add information to the log only

        forceDeleteFileQuietly(getPropertiesFileName(dirId));
        forceDeleteFileQuietly(getLdapFileName(dirId));
    }


    public Map<String, Object> createOpenLdapUserTemplateFiles(String templateId, Map<String, String> props)
            throws AcmLdapConfigurationException, IOException
    {
        String propertiesFileName = ldapConfigurationLocation + String.format(openLdapUserPropertiesTemplateName, templateId);
        String fileName = ldapConfigurationLocation + String.format(openLdapUserFileTemplateName, templateId);

        return saveUserTemplateFiles(propertiesFileName, openLdapUserPropertiesTemplate, fileName,
                openLdapUserFileTemplate, props, templateId);
    }

    public Map<String, Object> createActiveDirectoryUserTemplateFiles(String templateId, Map<String, String> props)
            throws AcmLdapConfigurationException, IOException
    {
        String propertiesFileName = ldapConfigurationLocation + String.format(activeDirectoryUserPropertiesTemplateName, templateId);
        String fileName = ldapConfigurationLocation + String.format(activeDirectoryUserFileTemplateName, templateId);

        return saveUserTemplateFiles(propertiesFileName, activeDirectoryUserPropertiesTemplate, fileName,
                activeDirectoryUserFileTemplate, props, templateId);
    }

    public Map<String, Object> saveUserTemplateFiles(String propertiesFileName, String propertiesTemplateName, String fileName,
                                                     String fileTemplateName, Map<String, String> props, String templateId)
            throws AcmLdapConfigurationException
    {
        Map<String, Object> ldapAddUserMapAttributes = createLdapAddUserMapAttributes(props, templateId);
        try
        {
            writeTemplateFile(propertiesTemplateName, propertiesFileName, ldapAddUserMapAttributes);
            writeTemplateFile(fileTemplateName, fileName, ldapAddUserMapAttributes);
            return ldapAddUserMapAttributes;
        } catch (Exception e)
        {
            // Delete created files quietly
            FileUtils.deleteQuietly(new File(propertiesFileName));
            FileUtils.deleteQuietly(new File(fileName));
            log.error("Can't create LDAP properties template with name:{} and xml file with name:{} ",
                    propertiesFileName, fileName, e);
            throw new AcmLdapConfigurationException(String.format("Can't create LDAP configuration files:'%s', '%s'",
                    propertiesFileName, fileName), e);
        }
    }

    public Map<String, Object> createLdapAddUserMapAttributes(Map<String, String> userDefinedProperties, String id)
    {
        // first set any attribute for which the user has defined
        Map<String, Object> ldapAttributes = userDefinedProperties.entrySet().stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                .filter(entry -> ldapUserPropertiesFile.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        ldapAttributes.put("id", id);

        // add the rest of the attributes from the properties file with constants values which will be replaced later
        ldapUserPropertiesFile.entrySet().
                forEach(
                        propEntry -> ldapAttributes.putIfAbsent(propEntry.getKey().toString(),
                                propEntry.getValue().toString())
                );
        return ldapAttributes;
    }

    /**
     * Create Properties file
     *
     * @param dirId Directory identifier
     * @param props Directory properties data
     * @throws AcmLdapConfigurationException
     * @throws IOException
     */
    private void createPropertiesFile(String dirId, Map<String, Object> props) throws AcmLdapConfigurationException, IOException
    {
        if (propertiesFileExist(dirId))
        {
            throw new AcmLdapConfigurationException(String.format("Properties file with ID='%s' is present in the system.", dirId));
        }

        writePropertiesFile(dirId, props);
    }

    /**
     * Write data into the properties file
     *
     * @param dirId
     * @param props
     * @throws IOException
     * @throws AcmLdapConfigurationException
     */
    private void writePropertiesFile(String dirId, Map<String, Object> props) throws AcmLdapConfigurationException
    {
        String propertiesFileName = getPropertiesFileName(dirId);

        try
        {
            // Create Properties file
            writeTemplateFile(ldapTemplatePropertiesFile, propertiesFileName, props);
        } catch (Exception e)
        {
            log.error("Can't write LDAP properties with ID '{}' ", dirId, e);
            throw new AcmLdapConfigurationException("Can't write LDAP properties file ", e);
        }
    }

    private void writeTemplateFile(String templateName, String fileName, Map<String, Object> props)
            throws IOException, TemplateException
    {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));

        // LDAP file
        Template tmplSig = cfg.getTemplate(templateName);
        Writer writerSig = null;
        try
        {
            writerSig = new FileWriter(new File(fileName));
            tmplSig.process(props, writerSig);
        } finally
        {
            if (writerSig != null)
            {
                writerSig.close();
            }
        }
    }

    /**
     * Create LDAP file
     *
     * @param dirId Directory identifier
     * @param props Directory properties data
     * @throws IOException
     * @throws AcmLdapConfigurationException
     */
    private void createLdapFile(String dirId, Map<String, Object> props) throws IOException, AcmLdapConfigurationException
    {
        String ldapFileName = getLdapFileName(dirId);
        if (ldapFileExist(dirId))
        {
            throw new AcmLdapConfigurationException(String.format("LDAP file '%s' is present in the system.", ldapFileName));
        }

        try
        {
            writeTemplateFile(ldapTemplateFile, ldapFileName, props);
        } catch (Exception e)
        {
            log.error("Can't create LDAP file with ID '{}' ", dirId, e);
            throw new AcmLdapConfigurationException("Can't create LDAP file ", e);
        }
    }

    /**
     * Convert LDAP JSON Object to properties map
     *
     * @param jsonObj
     * @return
     * @throws AcmEncryptionException
     * @throws JSONException
     */
    public HashMap<String, Object> getProperties(JSONObject jsonObj) throws JSONException, AcmEncryptionException
    {
        HashMap<String, Object> props = new HashMap<>();
        props.put(LdapConfigurationProperties.LDAP_PROP_ID, jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ID));
        props.put("id", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ID));
        props.put("base", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_BASE));
        props.put("authUserDn", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_DN));
        props.put("authUserPassword",
                encryptablePropertyUtils.encryptPropertyValue(jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_PASSWORD)));
        props.put("userSearchBase", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_SEARCH_BASE));
        props.put("groupSearchBase", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_BASE));
        props.put("userSearchFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_SEARCH_FILTER));
        props.put("allUsersFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_USERS_FILTER));
        props.put("allUsersPageFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_USERS_PAGE_FILTER));
        props.put("allUsersSortingAttribute", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_USERS_SORT_ATTRIBUTE));
        props.put("groupSearchFilterForUser", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_FILTER_FOR_USER));
        props.put("groupSearchFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_FILTER));
        props.put("groupSearchPageFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_PAGE_FILTER));
        props.put("groupsSortingAttribute", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUPS_SORT_ATTRIBUTE));
        props.put("ldapUrl", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_LDAP_URL));
        props.put("enableEditingLdapUsers", jsonObj.getBoolean(LdapConfigurationProperties.LDAP_PROP_ENABLE_EDITING_LDAP_USERS));
        props.put("syncPageSize", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_SYNC_PAGE_SIZE));
        props.put("userIdAttributeName", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_ID_ATTR_NAME));
        props.put("userDomain", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_USER_DOMAIN)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_DOMAIN) : "");
        return props;
    }

    public List<File> getPropertiesFiles()
    {
        // Get All properties files
        String[] extensions = new String[]{"properties"};
        List<File> files = (List<File>) FileUtils.listFiles(new File(ldapConfigurationLocation), extensions, false);
        List<File> propertiesFiles = new ArrayList<>();

        // Get all properties files that match to ldapPropertiesFileRegex
        Pattern pattern = Pattern.compile(ldapPropertiesFileRegex);

        for (File fileIter : files)
        {
            String fileName = fileIter.getName();
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find())
            {
                propertiesFiles.add(fileIter);
            }
        }

        return propertiesFiles;
    }

    private void forceDeleteFileQuietly(String fileName)
    {
        try
        {
            FileUtils.forceDelete(new File(fileName));
        } catch (IOException e)
        {
            log.error("Can't delete file {} ", fileName, e);
        }
    }

    private void deletePropertiesFileQuietly(String dirId)
    {
        FileUtils.deleteQuietly(new File(getPropertiesFileName(dirId)));
    }

    private void deleteLdapFileQuietly(String dirId)
    {
        FileUtils.deleteQuietly(new File(getLdapFileName(dirId)));
    }

    public String getPropertiesFileName(String dirId)
    {
        return ldapConfigurationLocation + String.format(ldapPropertiesFile, dirId);
    }

    public String getLdapFileName(String dirId)
    {
        return ldapConfigurationLocation + String.format(ldapFile, dirId);
    }

    public boolean propertiesFileExist(String dirId)
    {
        String fileName = getPropertiesFileName(dirId);
        return new File(fileName).exists();
    }

    public boolean ldapFileExist(String dirId)
    {
        String fileName = getLdapFileName(dirId);
        return new File(fileName).exists();
    }

    public void setLdapConfigurationLocation(String ldapConfigurationLocation)
    {
        this.ldapConfigurationLocation = ldapConfigurationLocation;
    }

    public void setLdapFile(String ldapFile)
    {
        this.ldapFile = ldapFile;
    }

    public void setLdapPropertiesFile(String ldapPropertiesFile)
    {
        this.ldapPropertiesFile = ldapPropertiesFile;
    }

    public void setLdapConfigurationTemplatesLocation(String ldapConfigurationTemplatesLocation)
    {
        this.ldapConfigurationTemplatesLocation = ldapConfigurationTemplatesLocation;
    }

    public void setLdapTemplateFile(String ldapTemplateFile)
    {
        this.ldapTemplateFile = ldapTemplateFile;
    }

    public void setLdapTemplatePropertiesFile(String ldapTemplatePropertiesFile)
    {
        this.ldapTemplatePropertiesFile = ldapTemplatePropertiesFile;
    }

    public void setLdapPropertiesFileRegex(String ldapPropertiesFileRegex)
    {
        this.ldapPropertiesFileRegex = ldapPropertiesFileRegex;
    }

    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }

    public Properties getLdapUserPropertiesFile()
    {
        return ldapUserPropertiesFile;
    }

    public void setLdapUserPropertiesFile(Properties ldapUserPropertiesFile)
    {
        this.ldapUserPropertiesFile = ldapUserPropertiesFile;
    }

    public void setOpenLdapUserPropertiesTemplate(String openLdapUserPropertiesTemplate)
    {
        this.openLdapUserPropertiesTemplate = openLdapUserPropertiesTemplate;
    }

    public void setOpenLdapUserFileTemplate(String openLdapUserFileTemplate)
    {
        this.openLdapUserFileTemplate = openLdapUserFileTemplate;
    }

    public void setOpenLdapUserPropertiesTemplateName(String openLdapUserPropertiesTemplateName)
    {
        this.openLdapUserPropertiesTemplateName = openLdapUserPropertiesTemplateName;
    }

    public void setOpenLdapUserFileTemplateName(String openLdapUserFileTemplateName)
    {
        this.openLdapUserFileTemplateName = openLdapUserFileTemplateName;
    }

    public void setActiveDirectoryUserPropertiesTemplate(String activeDirectoryUserPropertiesTemplate)
    {
        this.activeDirectoryUserPropertiesTemplate = activeDirectoryUserPropertiesTemplate;
    }

    public void setActiveDirectoryUserFileTemplate(String activeDirectoryUserFileTemplate)
    {
        this.activeDirectoryUserFileTemplate = activeDirectoryUserFileTemplate;
    }

    public void setActiveDirectoryUserPropertiesTemplateName(String activeDirectoryUserPropertiesTemplateName)
    {
        this.activeDirectoryUserPropertiesTemplateName = activeDirectoryUserPropertiesTemplateName;
    }

    public void setActiveDirectoryUserFileTemplateName(String activeDirectoryUserFileTemplateName)
    {
        this.activeDirectoryUserFileTemplateName = activeDirectoryUserFileTemplateName;
    }
}
