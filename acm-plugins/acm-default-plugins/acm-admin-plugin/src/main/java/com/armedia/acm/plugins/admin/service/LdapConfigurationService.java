package com.armedia.acm.plugins.admin.service;

/*-
 * #%L
 * ACM Default Plugin: admin
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;
import com.armedia.acm.plugins.admin.model.LdapConfigurationProperties;
import com.armedia.acm.plugins.admin.model.LdapDirectoryConfig;
import com.armedia.acm.plugins.admin.model.LdapTemplateConfig;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.CronExpression;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Created by sergey.kolomiets on 6/2/15.
 */
public class LdapConfigurationService implements InitializingBean
{
    private Logger log = LogManager.getLogger(LdapConfigurationService.class);

    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    private LdapDirectoryConfig ldapDirectoryConfig;

    private Map<String, LdapTemplateConfig> templatesConfigMap;

    private Pattern ldapConfigurationLocationPattern;

    private Pattern ldapDirectoryNamePattern;

    private Pattern ldapUserFilePattern;

    private Pattern ldapUserPropertiesFilePattern;

    private Pattern ldapGroupFilePattern;

    private Pattern ldapGroupPropertiesFilePattern;

    private Pattern ldapPropertiesFilePattern;

    private SpringContextHolder contextHolder;

    /**
     * Create LDAP Directory config files
     *
     * @param dirId
     *            Directory identifier
     * @param props
     *            Directory properties data
     * @throws AcmLdapConfigurationException
     */
    public void createLdapDirectory(String dirId, Map<String, Object> props) throws AcmLdapConfigurationException
    {

        Matcher matcher = ldapDirectoryNamePattern.matcher(dirId);
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
        }
        catch (Exception e)
        {
            // Delete created files quietly
            deletePropertiesFileQuietly(dirId);
            deleteLdapFileQuietly(dirId);

            log.error("Can't create LDAP directory [{}] ", dirId, e);
            throw new AcmLdapConfigurationException(String.format("Can't create LDAP directory with ID='%s'", dirId), e);
        }
    }

    /**
     * Update LDAP Directory settings
     *
     * @param dirId
     *            Directory identifier
     * @param props
     *            Directory properties data
     */
    public void updateLdapDirectory(String dirId, Map<String, Object> props) throws AcmLdapConfigurationException
    {
        String propertiesFileName = getPropertiesFileName(dirId);

        if (!propertiesFileExist(dirId))
        {
            throw new AcmLdapConfigurationException(String.format("Can't find property file '%s'", propertiesFileName));
        }

        writePropertiesFile(dirId, props);
        String ldapFileName = getLdapFileName(dirId);
        File ldapFile = new File(ldapFileName);
        try
        {
            contextHolder.replaceContextFromFile(ldapFile);
        }
        catch (IOException e)
        {
            log.error("Could not add context from file: [{}]. ", ldapFile.getName(), e);
        }
    }

    /**
     * Delete LDAP Directory
     *
     * @param dirId
     *            Directory identifier
     * @throws AcmLdapConfigurationException
     */
    public void deleteLdapDirectory(String dirId) throws AcmLdapConfigurationException
    {
        String[] extensions = new String[] { "properties" };
        List<File> propertiesFiles = (List<File>) FileUtils.listFiles(
                new File(ldapDirectoryConfig.getLdapConfigurationLocation()), extensions, false);

        long matchedFiles = propertiesFiles.stream()
                .filter(file -> ldapPropertiesFilePattern.matcher(file.getName()).find())
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

    public Map<String, Object> createLdapAddUserTemplateFiles(String templateId, String templateType, Map<String, String> props)
            throws AcmLdapConfigurationException
    {
        LdapTemplateConfig templateConfig = templatesConfigMap.get(templateType.toLowerCase());

        if (templateConfig != null)
        {
            String propertiesFileName = ldapDirectoryConfig.getLdapConfigurationLocation()
                    + String.format(templateConfig.getUserPropertiesTemplateName(), templateId);
            String fileName = ldapDirectoryConfig.getLdapConfigurationLocation()
                    + String.format(templateConfig.getUserFileTemplateName(), templateId);
            return saveTemplateFiles(propertiesFileName, templateConfig.getUserPropertiesTemplate(), fileName,
                    templateConfig.getUserFileTemplate(), props, templateId, ldapDirectoryConfig.getLdapUserPropertiesFile());
        }
        log.warn("Can't create LDAP user template for [{}] directory with id: [{}", templateType, templateId);
        throw new AcmLdapConfigurationException(String.format("Can't create LDAP user template for %s-directory with id:'%s'",
                templateType, templateId), null);
    }

    public Map<String, Object> createLdapAddGroupTemplate(String templateId, String templateType,
            Map<String, String> props) throws AcmLdapConfigurationException
    {
        LdapTemplateConfig templateConfig = templatesConfigMap.get(templateType.toLowerCase());

        if (templateConfig != null)
        {
            String propertiesFileName = ldapDirectoryConfig.getLdapConfigurationLocation()
                    + String.format(templateConfig.getGroupPropertiesTemplateName(), templateId);
            String fileName = ldapDirectoryConfig.getLdapConfigurationLocation()
                    + String.format(templateConfig.getGroupFileTemplateName(), templateId);
            return saveTemplateFiles(propertiesFileName, templateConfig.getGroupPropertiesTemplate(), fileName,
                    templateConfig.getGroupFileTemplate(), props, templateId, ldapDirectoryConfig.getLdapGroupPropertiesFile());
        }
        log.debug("Can't create LDAP group template for [{}] directory with id: [{}]", templateType, templateId);
        throw new AcmLdapConfigurationException(String.format("Can't create LDAP group template for %s-directory with id:'%s'",
                templateType, templateId), null);
    }

    public Map<String, Object> saveTemplateFiles(String propertiesFileName, String propertiesTemplateName,
            String fileName, String fileTemplateName, Map<String, String> props,
            String templateId, Properties attributesTemplate)
            throws AcmLdapConfigurationException
    {
        Map<String, Object> ldapAddUserMapAttributes = createLdapTemplatesMapAttributes(props, templateId, attributesTemplate);
        File exportPropertiesFile = new File(propertiesFileName);
        boolean updatePropertiesFile = exportPropertiesFile.exists();
        try
        {
            if (updatePropertiesFile)
            {
                updateTemplateFile(propertiesTemplateName, propertiesFileName, ldapAddUserMapAttributes);
            }
            else
            {
                writeTemplateFile(propertiesTemplateName, propertiesFileName, ldapAddUserMapAttributes);
                writeTemplateFile(fileTemplateName, fileName, ldapAddUserMapAttributes);

            }
            return ldapAddUserMapAttributes;
        }
        catch (Exception e)
        {
            // Delete created files quietly
            // If the files exist, do not delete on failed update
            if (!updatePropertiesFile)
            {
                FileUtils.deleteQuietly(new File(propertiesFileName));
                FileUtils.deleteQuietly(new File(fileName));
            }
            log.error("Can't create LDAP properties template with name: [{}] and xml file with name: [{}] ",
                    propertiesFileName, fileName, e);
            throw new AcmLdapConfigurationException(String.format("Can't create LDAP configuration files:'%s', '%s'",
                    propertiesFileName, fileName), e);
        }
    }

    public Map<String, Object> createLdapTemplatesMapAttributes(Map<String, String> userDefinedProperties, String id,
            Properties attributesTemplate)
    {
        // set any attribute which the user has defined
        Map<String, Object> ldapAttributes = userDefinedProperties.entrySet().stream()
                .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                .filter(entry -> attributesTemplate.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        ldapAttributes.put("id", id);
        return ldapAttributes;
    }

    /**
     * Create Properties file
     *
     * @param dirId
     *            Directory identifier
     * @param props
     *            Directory properties data
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
        }
        catch (Exception e)
        {
            log.error("Can't write LDAP properties with ID [{}] ", dirId, e);
            throw new AcmLdapConfigurationException("Can't write LDAP properties file ", e);
        }
    }

    private void updateTemplateFile(String templateName, String fileName, Map<String, Object> props)
            throws AcmLdapConfigurationException
    {
        long timestamp = System.currentTimeMillis();
        String tempFileName = fileName + timestamp;

        try
        {
            writeTemplateFile(templateName, tempFileName, props);
        }
        catch (IOException | TemplateException e)
        {
            throw new AcmLdapConfigurationException("Can't write LDAP template file", e);
        }

        // delete old file only after successfully created new file
        FileUtils.deleteQuietly(new File(fileName));
        try
        {
            FileUtils.moveFile(new File(tempFileName), new File(fileName));
        }
        catch (IOException e)
        {
            log.warn("Renaming file from: [{}] to: [{}] failed ", tempFileName, fileName);
        }
    }

    private void writeTemplateFile(String templateName, String fileName, Map<String, Object> props)
            throws IOException, TemplateException
    {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(ldapDirectoryConfig.getLdapConfigurationTemplatesLocation()));

        // LDAP file
        Template tmplSig = cfg.getTemplate(templateName);
        try (Writer writerSig = new FileWriter(new File(fileName)))
        {
            tmplSig.process(props, writerSig);
        }
    }

    /**
     * Create LDAP file
     *
     * @param dirId
     *            Directory identifier
     * @param props
     *            Directory properties data
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
        }
        catch (Exception e)
        {
            log.error("Can't create LDAP file with ID [{}] ", dirId, e);
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
    public HashMap<String, Object> getProperties(JSONObject jsonObj)
            throws JSONException, AcmEncryptionException, AcmLdapConfigurationException
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
        props.put("allChangedUsersFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_CHANGED_USERS_FILTER));
        props.put("allUsersPageFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_USERS_PAGE_FILTER));
        props.put("allChangedUsersPageFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_CHANGED_USERS_PAGE_FILTER));
        props.put("allUsersSortingAttribute", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ALL_USERS_SORT_ATTRIBUTE));
        props.put("groupSearchFilterForUser", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_FILTER_FOR_USER));
        props.put("groupSearchFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_FILTER));
        props.put("changedGroupSearchFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_CHANGED_GROUP_SEARCH_FILTER));
        props.put("groupSearchPageFilter", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_PAGE_FILTER));
        props.put("changedGroupSearchPageFilter",
                jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_CHANGED_GROUP_SEARCH_PAGE_FILTER));
        props.put("groupsSortingAttribute", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUPS_SORT_ATTRIBUTE));
        props.put("ldapUrl", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_LDAP_URL));
        props.put("enableEditingLdapUsers", jsonObj.getBoolean(LdapConfigurationProperties.LDAP_PROP_ENABLE_EDITING_LDAP_USERS));
        props.put("syncEnabled", jsonObj.getBoolean(LdapConfigurationProperties.LDAP_PROP_SYNC_ENABLED));
        props.put("directoryType", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_DIRECTORY_TYPE).toLowerCase());
        props.put("syncPageSize", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_SYNC_PAGE_SIZE));
        props.put("userIdAttributeName", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_ID_ATTR_NAME));
        props.put("userDomain", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_USER_DOMAIN)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_DOMAIN)
                : "");
        props.put("userPrefix", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_USER_PREFIX)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_PREFIX)
                : "");
        props.put("groupPrefix", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_GROUP_PREFIX)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_PREFIX)
                : "");
        props.put("userControlGroup", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_USER_CONTROL_GROUP)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_CONTROL_GROUP)
                : "");
        props.put("groupControlGroup", jsonObj.has(LdapConfigurationProperties.LDAP_PROP_GROUP_CONTROL_GROUP)
                ? jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_CONTROL_GROUP)
                : "");
        if (jsonObj.has(LdapConfigurationProperties.LDAP_PARTIAL_SYNC_CRON))
        {
            String partialSyncCron = jsonObj.getString(LdapConfigurationProperties.LDAP_PARTIAL_SYNC_CRON);
            if (CronExpression.isValidExpression(partialSyncCron))
            {
                props.put("partialSyncCron", partialSyncCron);
            }
            else
            {
                throw new AcmLdapConfigurationException("Partial sync Cron: " + partialSyncCron + " is not valid.");
            }
        }
        if (jsonObj.has(LdapConfigurationProperties.LDAP_FULL_SYNC_CRON))
        {
            String fullSyncCron = jsonObj.getString(LdapConfigurationProperties.LDAP_FULL_SYNC_CRON);
            if (CronExpression.isValidExpression(fullSyncCron))
            {
                props.put("fullSyncCron", fullSyncCron);
            }
            else
            {
                throw new AcmLdapConfigurationException("Full sync Cron: " + fullSyncCron + " is not valid");
            }
        }

        return props;
    }

    public List<File> getPropertiesFiles()
    {
        List<File> files = getAllLdapConfigFiles(ldapDirectoryConfig.getLdapConfigurationLocation(), "properties");

        return files.stream()
                .filter(file -> ldapConfigurationLocationPattern.matcher(file.getName()).find())
                .collect(Collectors.toList());
    }

    private List<File> getAllLdapConfigFiles(String path, String... extensions)
    {
        // Get All properties files
        return (List<File>) FileUtils.listFiles(new File(path),
                extensions, false);
    }

    public Optional<File> getDirectoryConfigurationFiles(String directory, String configurationLocation, Pattern pattern)
    {
        List<File> files = getAllLdapConfigFiles(configurationLocation, "properties", "xml");
        Predicate<File> filterUserTemplatesPredicate = file -> pattern.matcher(file.getName()).find() && file.getName().contains(directory);
        return files.stream()
                .filter(filterUserTemplatesPredicate)
                .findFirst();
    }

    private void forceDeleteFileQuietly(String fileName)
    {
        try
        {
            log.debug("Deleting file: [{}]", fileName);
            FileUtils.forceDelete(new File(fileName));
        }
        catch (IOException e)
        {
            log.error("Can't delete file [{}] ", fileName, e);
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

    public void createLdapDirectoryConfigurations(String id, String directoryType, Map<String, Object> props)
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
            List<Pattern> deletePatterns = Arrays.asList(ldapGroupPropertiesFilePattern,
                    ldapGroupFilePattern, ldapUserPropertiesFilePattern, ldapUserFilePattern);

            deletePatterns.forEach(pattern -> getDirectoryConfigurationFiles(directoryId, configurationLocation, pattern)
                    .ifPresent(deleteFile));
        }
    }

    public String retrieveDirectoriesConfiguration() throws IOException
    {
        String configurationLocation = ldapDirectoryConfig.getLdapConfigurationLocation();
        List<File> propertiesFiles = getPropertiesFiles();

        Function<File, JSONObject> jsonTemplate = (File file) -> {
            Properties properties = new Properties();
            try (InputStream propInputStrem = FileUtils.openInputStream(file))
            {
                properties.load(propInputStrem);
            }
            catch (IOException e)
            {
                log.warn("Can not load template from file: [{}]", file, e);
            }
            return propertiesToJSONObject(properties);
        };

        JSONArray dirsJsonArr = new JSONArray();
        for (File propertyFile : propertiesFiles)
        {
            Properties prop = new Properties();
            try (InputStream propInputStream = FileUtils.openInputStream(propertyFile))
            {
                prop.load(propInputStream);

                // Put all properties into JSON Object
                JSONObject dirJsonObj = propertiesToJSONObject(prop);

                String directory = prop.getProperty(LdapConfigurationProperties.LDAP_PROP_ID);

                getDirectoryConfigurationFiles(directory, configurationLocation,
                        ldapUserPropertiesFilePattern)
                                .map(jsonTemplate)
                                .ifPresent(
                                        jsonObject -> dirJsonObj.put(LdapConfigurationProperties.LDAP_PROP_ADD_USER_TEMPLATE, jsonObject));

                getDirectoryConfigurationFiles(directory, configurationLocation,
                        ldapGroupPropertiesFilePattern)
                                .map(jsonTemplate)
                                .ifPresent(
                                        jsonObject -> dirJsonObj.put(LdapConfigurationProperties.LDAP_PROP_ADD_GROUP_TEMPLATE, jsonObject));

                dirsJsonArr.put(dirJsonObj);
            }
        }
        return dirsJsonArr.toString();
    }

    public JSONObject propertiesToJSONObject(Properties properties)
    {
        JSONObject jsonProperties = new JSONObject();
        Set<String> propertyNames = properties.stringPropertyNames();
        propertyNames.forEach(property -> jsonProperties.put(property, properties.getProperty(property)));
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

    public Map<String, LdapTemplateConfig> getTemplatesConfigMap()
    {
        return templatesConfigMap;
    }

    public void setTemplatesConfigMap(Map<String, LdapTemplateConfig> templatesConfigMap)
    {
        this.templatesConfigMap = templatesConfigMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.ldapConfigurationLocationPattern = Pattern.compile(ldapDirectoryConfig.getLdapPropertiesFileRegex());
        this.ldapDirectoryNamePattern = Pattern.compile("^[a-zA-Z0-9.]+$");
        this.ldapUserFilePattern = Pattern.compile(ldapDirectoryConfig.getLdapUserFileRegex());
        this.ldapUserPropertiesFilePattern = Pattern.compile(ldapDirectoryConfig.getLdapUserPropertiesFileRegex());
        this.ldapGroupFilePattern = Pattern.compile(ldapDirectoryConfig.getLdapGroupFileRegex());
        this.ldapGroupPropertiesFilePattern = Pattern.compile(ldapDirectoryConfig.getLdapGroupPropertiesFileRegex());
        this.ldapPropertiesFilePattern = Pattern.compile(ldapDirectoryConfig.getLdapPropertiesFileRegex());
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }
}
