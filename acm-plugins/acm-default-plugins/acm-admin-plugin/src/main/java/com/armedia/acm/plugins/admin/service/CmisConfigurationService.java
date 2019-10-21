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

import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;
import com.armedia.acm.plugins.admin.exception.AcmCmisConfigurationException;
import com.armedia.acm.plugins.admin.model.CmisConfigurationConstants;
import com.armedia.acm.plugins.admin.model.CmisUrlConfig;
import com.armedia.mule.cmis.basic.auth.HttpInvokerUtil;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

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

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Created by nick.ferguson on 3/22/2017.
 */
public class CmisConfigurationService
{
    private Logger log = LogManager.getLogger(getClass());
    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    private String cmisConfigurationLocation;
    private String cmisFile;
    private String cmisPropertiesFile;

    private String cmisConfigurationTemplatesLocation;
    private String cmisTemplateXmlFile;
    private String cmisTemplatePropertiesFile;

    private Pattern cmisIdPattern;
    private Pattern cmisPropertiesPattern;

    private CamelContextManager camelContextManager;

    /**
     * Create CMIS Config config files
     *
     * @param cmisId
     *            Config identifier
     * @param props
     *            Config properties data
     * @throws AcmCmisConfigurationException
     */
    public void createCmisConfig(String cmisId, Map<String, Object> props) throws AcmCmisConfigurationException
    {
        Matcher matcher = cmisIdPattern.matcher(cmisId);
        if (!matcher.find())
        {
            log.error(
                    "Unable to create configuration with ID '{}', ID is the wrong format. Only numbers, characters symbols and '.' are allowed",
                    cmisId);
            throw new AcmCmisConfigurationException("ID has wrong format. Only numbers, characters symbols and '.' are allowed");
        }

        // Check if CMIS files exist
        if (propertiesFileExist(cmisId) || cmisFileExist(cmisId))
        {
            log.error("CMIS config with ID '{}' already exists", cmisId);
            throw new AcmCmisConfigurationException(String.format("CMIS config with ID='%s' exists", cmisId));
        }

        try
        {
            log.debug("Attempting to create CMIS Configuration Properties File");
            createPropertiesFile(cmisId, props);

            log.debug("Attempting to create CMIS Configuration XML file");
            createCmisFile(cmisId, props);
            getCamelContextManager().updateRepositoryConfigs();
        }
        catch (Exception e)
        {
            log.error("Can't create CMIS config '{}' ", cmisId, e);
            log.debug("Cleaning up created files");

            // Delete created files quietly
            deletePropertiesFileQuietly(cmisId);
            deleteCmisFileQuietly(cmisId);

            throw new AcmCmisConfigurationException(String.format("Can't create CMIS config with ID='%s'", cmisId), e);
        }
    }

    /**
     * Update CMIS Config settings
     *
     * @param cmisId
     *            Config identifier
     * @param props
     *            Config properties data
     */
    public void updateCmisConfig(String cmisId, Map<String, Object> props) throws AcmCmisConfigurationException
    {
        String propertiesFileName = getPropertiesFileName(cmisId);

        if (!propertiesFileExist(cmisId))
        {
            throw new AcmCmisConfigurationException(String.format("Can't find property file '%s'", propertiesFileName));
        }

        writePropertiesFile(cmisId, props);
        getCamelContextManager().updateRepositoryConfigs();
    }

    /**
     * Delete CMIS Config
     *
     * @param cmisId
     *            Config identifier
     * @throws AcmCmisConfigurationException
     */
    public void deleteCmisConfig(String cmisId) throws AcmCmisConfigurationException
    {

        int matchedFiles = getPropertiesFiles().size();

        if (matchedFiles == 0)
        {
            throw new AcmCmisConfigurationException("There are no CMIS properties files");
        }

        if (matchedFiles == 1)
        {
            throw new AcmCmisConfigurationException("Can't delete last CMIS file");
        }

        // Delete CMIS config files. If something goes wrong then go ahead add information to the log only

        forceDeleteFileQuietly(getPropertiesFileName(cmisId));
        forceDeleteFileQuietly(getCmisFileName(cmisId));

        getCamelContextManager().updateRepositoryConfigs();
    }

    /**
     * Create Properties file
     *
     * @param cmisId
     *            Config identifier
     * @param props
     *            Config properties data
     * @throws AcmCmisConfigurationException
     * @throws IOException
     */
    private void createPropertiesFile(String cmisId, Map<String, Object> props) throws AcmCmisConfigurationException, IOException
    {
        if (propertiesFileExist(cmisId))
        {
            throw new AcmCmisConfigurationException(String.format("Properties file with ID='%s' is present in the system.", cmisId));
        }

        writePropertiesFile(cmisId, props);
    }

    private void writeFileFromTemplate(Map<String, Object> props, String fileTemplate, String fileName, String tempFileName)
            throws IOException
    {
        // Create Properties file
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(cmisConfigurationTemplatesLocation));

        Template tmplProperties = cfg.getTemplate(fileTemplate);

        File tempFile = new File(tempFileName);
        File targetFile = new File(fileName);

        try (Writer writerProp = new FileWriter(tempFile))
        {
            log.debug("Writing properties to temp file: '{}'", tempFileName);
            tmplProperties.process(props, writerProp);

            copyOverFile(tempFile, targetFile);
            log.debug("Temp file '{}' copied successfully.", tempFileName);

            log.debug("Deleting Temporary File: '{}'", tempFileName);
            deleteFileQuietly(tempFileName);
        }
        catch (Exception e)
        {
            log.error("Failed to write file from template '{}' ", fileTemplate, e);
        }
    }

    /**
     * Write data into the properties file
     *
     * @param cmisId
     * @param props
     * @throws IOException
     * @throws AcmCmisConfigurationException
     */
    private void writePropertiesFile(String cmisId, Map<String, Object> props) throws AcmCmisConfigurationException
    {
        String propertiesFileName = getPropertiesFileName(cmisId);
        String tempFileName = getTempPropertiesFileName(cmisId);

        try
        {
            // Create Properties file
            log.debug("Attempting to write CMIS Properties file with ID '{}' ", cmisId);
            writeFileFromTemplate(props, cmisTemplatePropertiesFile, propertiesFileName, tempFileName);
        }
        catch (Exception e)
        {
            log.error("Failed to write CMIS Properties file with ID '{}' ", cmisId, e);
            throw new AcmCmisConfigurationException("Can't write CMIS properties file ", e);
        }
    }

    /**
     * Create CMIS file
     *
     * @param cmisId
     *            Config identifier
     * @param props
     *            Config properties data
     * @throws IOException
     * @throws AcmCmisConfigurationException
     */
    private void createCmisFile(String cmisId, Map<String, Object> props) throws IOException, AcmCmisConfigurationException
    {
        String cmisFileName = getCmisFileName(cmisId);
        String tempFileName = getTempCmisFileName(cmisId);

        if (cmisFileExist(cmisId))
        {
            throw new AcmCmisConfigurationException(String.format("CMIS file '%s' is present in the system.", cmisFileName));
        }

        try
        {
            // CMIS file
            log.debug("Writing CMIS XML file with ID '{}' ", cmisId);
            writeFileFromTemplate(props, cmisTemplateXmlFile, cmisFileName, tempFileName);

        }
        catch (Exception e)
        {
            log.error("Can't create CMIS file with ID '{}' ", cmisId, e);
            throw new AcmCmisConfigurationException("Can't create CMIS file ", e);
        }
    }

    /**
     * Convert CMIS JSON Object to properties map
     *
     * @param jsonObj
     * @return
     * @throws AcmEncryptionException
     * @throws JSONException
     */
    public HashMap<String, Object> getProperties(JSONObject jsonObj) throws JSONException, AcmEncryptionException
    {
        HashMap<String, Object> props = new HashMap<>();
        props.put("id", jsonObj.getString(CmisConfigurationConstants.CMIS_ID));
        props.put("baseUrl", jsonObj.getString(CmisConfigurationConstants.CMIS_BASEURL));
        props.put("username", jsonObj.getString(CmisConfigurationConstants.CMIS_USERNAME));
        props.put("password", encryptablePropertyUtils.encryptPropertyValue(jsonObj.getString(CmisConfigurationConstants.CMIS_PASSWORD)));
        props.put("useAlfrescoExtension", jsonObj.getString(CmisConfigurationConstants.CMIS_USEALFRESCOEXTENSION));
        props.put("endpoint", CmisConfigurationConstants.CMIS_ENDPOINT);
        props.put("maxIdle", jsonObj.getInt(CmisConfigurationConstants.CMIS_MAXIDLE));
        props.put("maxActive", jsonObj.getInt(CmisConfigurationConstants.CMIS_MAXACTIVE));
        props.put("maxWait", jsonObj.getInt(CmisConfigurationConstants.CMIS_MAXWAIT));
        props.put("minEvictionMillis", jsonObj.getInt(CmisConfigurationConstants.CMIS_MINEVICTIONMILLIS));
        props.put("evictionCheckIntervalMillis", jsonObj.getInt(CmisConfigurationConstants.CMIS_EVICTIONCHECKINTERVALMILLIS));
        props.put("reconnectCount", jsonObj.getInt(CmisConfigurationConstants.CMIS_RECONNECTCOUNT));
        props.put("reconnectFrequency", jsonObj.getInt(CmisConfigurationConstants.CMIS_RECONNECTFREQUENCY));
        props.put("repositoryId", jsonObj.has(CmisConfigurationConstants.CMIS_REPOSITORYID)
                ? jsonObj.getString(CmisConfigurationConstants.CMIS_REPOSITORYID)
                : "");
        props.put("cmisVersioningState", jsonObj.getString(CmisConfigurationConstants.CMIS_VERSIONINGSTATE));

        return props;
    }

    public List<File> getPropertiesFiles()
    {
        // Get All properties files
        String[] extensions = new String[] { "properties" };
        List<File> files = (List<File>) FileUtils.listFiles(new File(cmisConfigurationLocation), extensions, false);
        List<File> propertiesFiles = new ArrayList<>();

        for (File fileIter : files)
        {
            String fileName = fileIter.getName();
            Matcher matcher = cmisPropertiesPattern.matcher(fileName);
            if (matcher.find())
            {
                propertiesFiles.add(fileIter);
                log.debug("Found CMIS property file: '{}'", fileName);
            }
        }

        return propertiesFiles;
    }

    private void forceDeleteFileQuietly(String fileName)
    {
        log.debug("Attempting to delete " + fileName);
        try
        {
            FileUtils.forceDelete(new File(fileName));
        }
        catch (IOException e)
        {
            log.error("Can't delete file {} ", fileName, e);
        }
    }

    private void copyOverFile(File source, File target)
    {

        log.debug("Copying temp file '{}' to '{}'", source.getName(), target.getName());
        try
        {
            FileUtils.copyFile(source, target);
        }
        catch (IOException e)
        {
            log.error("Failed to copy file {} to {}", source.getName(), target.getName(), e);
        }
    }

    private void deletePropertiesFileQuietly(String cmisId)
    {
        FileUtils.deleteQuietly(new File(getPropertiesFileName(cmisId)));
    }

    private void deleteCmisFileQuietly(String cmisId)
    {
        FileUtils.deleteQuietly(new File(getCmisFileName(cmisId)));
    }

    private void deleteFileQuietly(String target)
    {
        FileUtils.deleteQuietly(new File(target));
    }

    public String getTempPropertiesFileName(String cmisId)
    {
        return FileUtils.getTempDirectoryPath() + String.format(cmisPropertiesFile, cmisId);
    }

    public String getTempCmisFileName(String cmisId)
    {
        return FileUtils.getTempDirectoryPath() + String.format(cmisFile, cmisId);
    }

    public String getPropertiesFileName(String cmisId)
    {
        return cmisConfigurationLocation + String.format(cmisPropertiesFile, cmisId);
    }

    public String getCmisFileName(String cmisId)
    {
        return cmisConfigurationLocation + String.format(cmisFile, cmisId);
    }

    public boolean propertiesFileExist(String cmisId)
    {
        String fileName = getPropertiesFileName(cmisId);
        log.debug("Checking if CMIS Properties file '{}' exists", fileName);
        return new File(fileName).exists();
    }

    public boolean cmisFileExist(String cmisId)
    {
        String fileName = getCmisFileName(cmisId);
        log.debug("Checking if CMIS Configuration file '{}' exists", fileName);
        return new File(fileName).exists();
    }

    public List<Repository> getRepositories(CmisUrlConfig cmisUrlConfig) throws AcmEncryptionException
    {

        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameters = new HashMap<>();
        parameters.put(SessionParameter.USER, cmisUrlConfig.getUsername());
        parameters.put(SessionParameter.PASSWORD, encryptablePropertyUtils.decryptPropertyValue(cmisUrlConfig.getPassword()));
        parameters.put(SessionParameter.REPOSITORY_ID, cmisUrlConfig.getRepositoryId());
        parameters.put(SessionParameter.HEADER, HttpInvokerUtil.EXTERNAL_AUTH_KEY + ": " + HttpInvokerUtil.getExternalUserIdValue());
        parameters.put(SessionParameter.ATOMPUB_URL, cmisUrlConfig.getBaseUrl());
        parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        return SessionFactoryImpl.newInstance().getRepositories(parameters);
    }

    public void setCmisConfigurationLocation(String cmisConfigurationLocation)
    {
        this.cmisConfigurationLocation = cmisConfigurationLocation;
    }

    public void setCmisFile(String cmisFile)
    {
        this.cmisFile = cmisFile;
    }

    public void setCmisPropertiesFile(String cmisPropertiesFile)
    {
        this.cmisPropertiesFile = cmisPropertiesFile;
    }

    public void setCmisConfigurationTemplatesLocation(String cmisConfigurationTemplatesLocation)
    {
        this.cmisConfigurationTemplatesLocation = cmisConfigurationTemplatesLocation;
    }

    public void setCmisTemplateXmlFile(String cmisTemplateXmlFile)
    {
        this.cmisTemplateXmlFile = cmisTemplateXmlFile;
    }

    public void setCmisTemplatePropertiesFile(String cmisTemplatePropertiesFile)
    {
        this.cmisTemplatePropertiesFile = cmisTemplatePropertiesFile;
    }

    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }

    public void setCmisIdPattern(Pattern cmisIdPattern)
    {
        this.cmisIdPattern = cmisIdPattern;
    }

    public void setCmisPropertiesPattern(Pattern cmisPropertiesPattern)
    {
        this.cmisPropertiesPattern = cmisPropertiesPattern;
    }

    public CamelContextManager getCamelContextManager()
    {
        return camelContextManager;
    }

    public void setCamelContextManager(CamelContextManager camelContextManager)
    {
        this.camelContextManager = camelContextManager;
    }
}
