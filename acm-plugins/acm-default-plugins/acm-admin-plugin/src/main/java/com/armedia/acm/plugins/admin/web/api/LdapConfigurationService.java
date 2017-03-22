package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import com.armedia.acm.plugins.admin.model.LdapDirectoryConfig;
import com.armedia.acm.plugins.admin.model.TemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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

    private LdapDirectoryConfig ldapDirectoryConfig;

    private Map<String, TemplateConfig> templatesConfigMap;

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
     * Delete LDAP Directory
     *
     * @param dirId Directory identifier
     * @throws AcmLdapConfigurationException
     */
    public void deleteLdapDirectory(String dirId) throws AcmLdapConfigurationException
    {
        String[] extensions = new String[]{"properties"};
        List<File> propertiesFiles = (List<File>) FileUtils.listFiles(new File(ldapDirectoryConfig.getLdapConfigurationLocation()), extensions, false);

        Pattern pattern = Pattern.compile(ldapDirectoryConfig.getLdapPropertiesFileRegex());
        long matchedFiles = propertiesFiles.stream()
                .filter(file -> pattern.matcher(file.getName()).find())
                .count();

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

    public Map<String, Object> createLdapAddUserTemplateFiles(String templateId, String
            templateType, Map<String, String> props)
            throws AcmLdapConfigurationException
    {
        TemplateConfig templateConfig = templatesConfigMap.get(templateType.toLowerCase());

        if (templateConfig != null)
        {
            String propertiesFileName = ldapDirectoryConfig.getLdapConfigurationLocation()
                    + String.format(templateConfig.getUserPropertiesTemplateName(), templateId);
            String fileName = ldapDirectoryConfig.getLdapConfigurationLocation()
                    + String.format(templateConfig.getUserFileTemplateName(), templateId);
            return saveTemplateFiles(propertiesFileName, templateConfig.getUserPropertiesTemplate(), fileName,
                    templateConfig.getUserFileTemplate(), props, templateId);
        }
        log.debug("Can't create LDAP user template for {}-directory with id:{}", templateType, templateId);
        throw new AcmLdapConfigurationException(String.format("Can't create LDAP user template for %s-directory with id:'%s'",
                templateType, templateId), null);
    }


    public Map<String, Object> createLdapAddGroupTemplate(String templateId, String templateType,
                                                          Map<String, String> props) throws AcmLdapConfigurationException
    {
        TemplateConfig templateConfig = templatesConfigMap.get(templateType.toLowerCase());

        if (templateConfig != null)
        {
            String propertiesFileName = ldapDirectoryConfig.getLdapConfigurationLocation()
                    + String.format(templateConfig.getGroupPropertiesTemplateName(), templateId);
            String fileName = ldapDirectoryConfig.getLdapConfigurationLocation()
                    + String.format(templateConfig.getGroupFileTemplateName(), templateId);
            return saveTemplateFiles(propertiesFileName, templateConfig.getGroupPropertiesTemplate(), fileName,
                    templateConfig.getGroupFileTemplate(), props, templateId);
        }
        log.debug("Can't create LDAP group template for {}-directory with id:{}", templateType, templateId);
        throw new AcmLdapConfigurationException(String.format("Can't create LDAP group template for %s-directory with id:'%s'",
                templateType, templateId), null);
    }

    public Map<String, Object> saveTemplateFiles(String propertiesFileName, String propertiesTemplateName, String
            fileName,
                                                 String fileTemplateName, Map<String, String> props, String templateId)
            throws AcmLdapConfigurationException
    {
        Map<String, Object> ldapAddUserMapAttributes = createLdapTemplatesMapAttributes(props, templateId);
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

    public Map<String, Object> createLdapTemplatesMapAttributes
            (Map<String, String> userDefinedProperties, String id)
    {
        // first set any attribute for which the user has defined
        Map<String, Object> ldapAttributes = userDefinedProperties.entrySet().stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                .filter(entry -> ldapDirectoryConfig.getLdapUserPropertiesFile().containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        ldapAttributes.put("id", id);

        // add the rest of the attributes from the properties file with constants values which will be replaced later
        ldapDirectoryConfig.getLdapUserPropertiesFile().entrySet().
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
            writeTemplateFile(ldapDirectoryConfig.getLdapTemplatePropertiesFile(), propertiesFileName, props);
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
        cfg.setDirectoryForTemplateLoading(new File(ldapDirectoryConfig.getLdapConfigurationTemplatesLocation()));

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
            writeTemplateFile(ldapDirectoryConfig.getLdapTemplateFile(), ldapFileName, props);
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
        props.put("directoryType", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_DIRECTORY_TYPE).toLowerCase());
        props.put("syncPageSize", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_SYNC_PAGE_SIZE));
        props.put("userIdAttributeName", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_ID_ATTR_NAME));
        props.put("userDomain", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_USER_DOMAIN)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_DOMAIN) : "");
        return props;
    }

    public List<File> getPropertiesFiles()
    {
        List<File> files = getAllLdapConfigFiles(ldapDirectoryConfig.getLdapConfigurationLocation(), "properties");

        // Get all properties files that match to ldapPropertiesFileRegex
        Pattern pattern = Pattern.compile(ldapDirectoryConfig.getLdapPropertiesFileRegex());

        return files.stream()
                .filter(file -> pattern.matcher(file.getName()).find())
                .collect(Collectors.toList());
    }

    private List<File> getAllLdapConfigFiles(String path, String... extensions)
    {
        // Get All properties files
        //String[] extensions = new String[]{"properties"};
        return (List<File>) FileUtils.listFiles(new File(path),
                extensions, false);
    }

    public Optional<File> getDirectoryConfigurationFiles(String directory, String configurationLocation, String fileRegex)
    {
        List<File> files = getAllLdapConfigFiles(configurationLocation, "properties", "xml");
        Pattern pattern = Pattern.compile(fileRegex);
        Predicate<File> filterUserTemplatesPredicate = file ->
                pattern.matcher(file.getName()).find() && file.getName().contains(directory);
        return files.stream()
                .filter(filterUserTemplatesPredicate)
                .findFirst();
    }

    private void forceDeleteFileQuietly(String fileName)
    {
        try
        {
            log.debug("Deleting file:{}", fileName);
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
        return ldapDirectoryConfig.getLdapConfigurationLocation() + String.format(ldapDirectoryConfig.getLdapPropertiesFile(), dirId);
    }

    public String getLdapFileName(String dirId)
    {
        return ldapDirectoryConfig.getLdapConfigurationLocation() + String.format(ldapDirectoryConfig.getLdapFile(), dirId);
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

    public void createLdapDirectoryConfigurations(String id, String directoryType, HashMap<String, Object> props)
            throws AcmLdapConfigurationException
    {
        createLdapDirectory(id, props);
        createLdapAddUserTemplateFiles(id, directoryType, new HashMap<>());
        createLdapAddGroupTemplate(id, directoryType, new HashMap<>());
    }

    public void deleteLdapDirectoryConfigurationFiles(String directoryId) throws AcmLdapConfigurationException
    {

        deleteLdapDirectory(directoryId);
        File deletedFile = new File(ldapDirectoryConfig.getLdapConfigurationLocation() +
                String.format(ldapDirectoryConfig.getLdapFile(), directoryId));
        if (!deletedFile.exists())
        {
            String configurationLocation = ldapDirectoryConfig.getLdapConfigurationLocation();
            Consumer<File> deleteFile = file -> forceDeleteFileQuietly(configurationLocation + file.getName());
            List<String> deleteRegex = Arrays.asList(ldapDirectoryConfig.getLdapGroupPropertiesFileRegex(),
                    ldapDirectoryConfig.getLdapGroupFileRegex(),
                    ldapDirectoryConfig.getLdapUserPropertiesFileRegex(),
                    ldapDirectoryConfig.getLdapUserFileRegex());

            deleteRegex.forEach(regex ->
                    getDirectoryConfigurationFiles(directoryId, configurationLocation, regex)
                            .ifPresent(deleteFile));
        }
    }

    public String retrieveDirectoriesConfiguration() throws IOException
    {
        String configurationLocation = ldapDirectoryConfig.getLdapConfigurationLocation();
        List<File> propertiesFiles = getPropertiesFiles();

        Function<File, JSONObject> jsonTemplate = (File file) ->
        {
            Properties properties = new Properties();
            try
            {
                properties.load(FileUtils.openInputStream(file));
            } catch (IOException e)
            {
                log.warn("Can not load template from file: {}", file, e);
            }
            return propertiesToJSONObject(properties);
        };

        JSONArray dirsJsonArr = new JSONArray();
        for (File propertyFile : propertiesFiles)
        {
            Properties prop = new Properties();
            prop.load(FileUtils.openInputStream(propertyFile));

            // Put all properties into JSON Object
            JSONObject dirJsonObj = propertiesToJSONObject(prop);

            String directory = prop.getProperty(LdapConfigurationProperties.LDAP_PROP_ID);


            getDirectoryConfigurationFiles(directory, configurationLocation,
                    ldapDirectoryConfig.getLdapUserPropertiesFileRegex())
                    .map(jsonTemplate)
                    .ifPresent(jsonObject ->
                            dirJsonObj.put(LdapConfigurationProperties.LDAP_PROP_ADD_USER_TEMPLATE, jsonObject));

            getDirectoryConfigurationFiles(directory, configurationLocation,
                    ldapDirectoryConfig.getLdapGroupPropertiesFileRegex())
                    .map(jsonTemplate)
                    .ifPresent(jsonObject -> dirJsonObj.put(LdapConfigurationProperties.LDAP_PROP_ADD_GROUP_TEMPLATE, jsonObject));

            dirsJsonArr.put(dirJsonObj);
        }
        return dirsJsonArr.toString();
    }

    public JSONObject propertiesToJSONObject(Properties properties)
    {
        JSONObject jsonProperties = new JSONObject();
        Set<String> propertyNames = properties.stringPropertyNames();
        propertyNames.forEach(property ->
                jsonProperties.put(property, properties.getProperty(property))
        );
        return jsonProperties;
    }

    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }

    public LdapDirectoryConfig getLdapDirectoryConfig()
    {
        return ldapDirectoryConfig;
    }

    public void setLdapDirectoryConfig(LdapDirectoryConfig ldapDirectoryConfig)
    {
        this.ldapDirectoryConfig = ldapDirectoryConfig;
    }

    public Map<String, TemplateConfig> getTemplatesConfigMap()
    {
        return templatesConfigMap;
    }

    public void setTemplatesConfigMap(Map<String, TemplateConfig> templatesConfigMap)
    {
        this.templatesConfigMap = templatesConfigMap;
    }
}
