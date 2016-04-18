package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.plugins.admin.exception.AcmLdapConfigurationException;

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

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Created by sergey.kolomiets on 6/2/15.
 */
public class LdapConfigurationService
{
    private Logger log = LoggerFactory.getLogger(LdapConfigurationService.class);

    private String ldapConfigurationLocation;
    private String ldapAuthFile;
    private String ldapSyncFile;
    private String ldapPropertiesFile;

    private String ldapConfigurationTemplatesLocation;
    private String ldapTemplateAuthFile;
    private String ldapTemplateSyncFile;
    private String ldapTemplatePropertiesFile;

    private String ldapPropertiesFileRegex;

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

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher matcher = pattern.matcher(dirId);
        if (!matcher.find())
        {
            throw new AcmLdapConfigurationException("ID has wrong format. Only numbers and characters symbols are allowed");
        }

        // Check if LDAP files are exist
        if (propertiesFileExist(dirId) || syncFileExist(dirId) || signatureFileExist(dirId))
        {
            throw new AcmLdapConfigurationException(String.format("LDAP directory with ID='%s' exists", dirId));
        }

        try
        {
            createPropertiesFile(dirId, props);
            createAuthFile(dirId, props);
            createSyncFile(dirId, props);
        }
        catch (Exception e)
        {
            // Delete created files quietly
            deletePropertiesFileQuietly(dirId);
            deleteAuthFileQuietly(dirId);
            deleteSyncFileQuietly(dirId);

            if (log.isErrorEnabled())
            {
                log.error(String.format("Can't create LDAP directory '%s'", dirId), e);
            }
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
    }

    /**
     * Delete LDAP Direcory
     *
     * @param dirId
     *            Directory identifier
     * @throws AcmLdapConfigurationException
     */
    public void deleteLdapDirectory(String dirId) throws AcmLdapConfigurationException
    {

        String[] extensions = new String[] { "properties" };
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
        forceDeleteFileQuietly(getAuthFileName(dirId));
        forceDeleteFileQuietly(getSyncFileName(dirId));
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
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));

            Template tmplProperties = cfg.getTemplate(ldapTemplatePropertiesFile);

            Writer writerProp = null;
            try
            {
                writerProp = new FileWriter(new File(propertiesFileName));
                tmplProperties.process(props, writerProp);
            }
            finally
            {
                if (writerProp != null)
                {
                    writerProp.close();
                }
            }

        }
        catch (Exception e)
        {
            if (log.isErrorEnabled())
            {
                log.error(String.format("Can't write LDAP properties file with ID '%s'", dirId), e);
            }
            throw new AcmLdapConfigurationException("Can't write LDAP properties file ", e);
        }
    }

    /**
     * Create Auth file
     *
     * @param dirId
     *            Directory identifier
     * @param props
     *            Directory properties data
     * @throws IOException
     * @throws AcmLdapConfigurationException
     */
    private void createAuthFile(String dirId, Map<String, Object> props) throws IOException, AcmLdapConfigurationException
    {
        String signatureFileName = getAuthFileName(dirId);
        if (signatureFileExist(dirId))
        {
            throw new AcmLdapConfigurationException(String.format("Auth file '%s' is present in the system.", signatureFileName));
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));

        try
        {
            // Auth file
            Template tmplSig = cfg.getTemplate(ldapTemplateAuthFile);
            Writer writerSig = null;
            try
            {
                writerSig = new FileWriter(new File(signatureFileName));
                tmplSig.process(props, writerSig);
            }
            finally
            {
                if (writerSig != null)
                {
                    writerSig.close();
                }
            }
        }
        catch (Exception e)
        {
            if (log.isErrorEnabled())
            {
                log.error(String.format("Can't create LDAP signature file with ID '%s'", dirId), e);
            }
            throw new AcmLdapConfigurationException("Can't create LDAP signature file ", e);
        }
    }

    /**
     * Create Sync file
     *
     * @param dirId
     *            Directory identifier
     * @param props
     *            Directory properties data
     * @throws IOException
     * @throws AcmLdapConfigurationException
     */
    private void createSyncFile(String dirId, Map<String, Object> props) throws IOException, AcmLdapConfigurationException
    {
        String syncFileName = getSyncFileName(dirId);
        if (syncFileExist(dirId))
        {
            throw new AcmLdapConfigurationException(String.format("Sync file '%s' is present in the system.", syncFileName));
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(new File(ldapConfigurationTemplatesLocation));

        try
        {
            Template tmplSync = cfg.getTemplate(ldapTemplateSyncFile);
            Writer writerSync = null;
            try
            {
                writerSync = new FileWriter(new File(syncFileName));
                tmplSync.process(props, writerSync);
            }
            finally
            {
                if (writerSync != null)
                {
                    writerSync.close();
                }
            }
        }
        catch (Exception e)
        {
            if (log.isErrorEnabled())
            {
                log.error(String.format("Can't create LDAP sync file with ID '%s'", dirId), e);
            }

            throw new AcmLdapConfigurationException("Can't create LDAP sync file ", e);
        }
    }

    /**
     * Convert LDAP JSON Object to properties map
     *
     * @param jsonObj
     * @return
     */
    public HashMap<String, Object> getProperties(JSONObject jsonObj)
    {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put(LdapConfigurationProperties.LDAP_PROP_ID, jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ID));
        props.put("id", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_ID));
        props.put("name", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_NAME));
        props.put("base", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_BASE));
        props.put("directoryName", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_DIR_NAME));
        props.put("authUserDn", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_DN));
        props.put("authUserPassword", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_AUTH_USER_PASSWORD));
        props.put("userSearchBase", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_SEARCH_BASE));
        props.put("groupSearchBase", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_BASE));
        props.put("groupSearchBaseOU", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_GROUP_SEARCH_BASE_OU));
        props.put("ldapUrl", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_LDAP_URL));
        props.put("userIdAttributeName", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_ID_ATTR_NAME));
        props.put("userDomain", jsonObj.getString(LdapConfigurationProperties.LDAP_PROP_USER_DOMAIN));
        return props;
    }

    public List<File> getPropertiesFiles()
    {
        // Get All properties files
        String[] extensions = new String[] { "properties" };
        List<File> files = (List<File>) FileUtils.listFiles(new File(ldapConfigurationLocation), extensions, false);
        List<File> propertiesFiles = new ArrayList<File>();

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
        }
        catch (IOException e)
        {
            if (log.isErrorEnabled())
            {
                log.error(String.format("Can't delete file %s ", fileName), e);
            }
        }
    }

    private void deletePropertiesFileQuietly(String dirId)
    {
        FileUtils.deleteQuietly(new File(getPropertiesFileName(dirId)));
    }

    private void deleteAuthFileQuietly(String dirId)
    {
        FileUtils.deleteQuietly(new File(getAuthFileName(dirId)));
    }

    private void deleteSyncFileQuietly(String dirId)
    {
        FileUtils.deleteQuietly(new File(getSyncFileName(dirId)));
    }

    public String getPropertiesFileName(String dirId)
    {
        return ldapConfigurationLocation + String.format(ldapPropertiesFile, dirId);
    }

    public String getAuthFileName(String dirId)
    {
        return ldapConfigurationLocation + String.format(ldapAuthFile, dirId);
    }

    public String getSyncFileName(String dirId)
    {
        return ldapConfigurationLocation + String.format(ldapSyncFile, dirId);
    }

    public boolean propertiesFileExist(String dirId)
    {
        String fileName = getPropertiesFileName(dirId);
        return new File(fileName).exists();
    }

    public boolean signatureFileExist(String dirId)
    {
        String fileName = getAuthFileName(dirId);
        return new File(fileName).exists();
    }

    public boolean syncFileExist(String dirId)
    {
        String fileName = getSyncFileName(dirId);
        return new File(fileName).exists();
    }

    public void setLdapConfigurationLocation(String ldapConfigurationLocation)
    {
        this.ldapConfigurationLocation = ldapConfigurationLocation;
    }

    public void setLdapAuthFile(String ldapAuthFile)
    {
        this.ldapAuthFile = ldapAuthFile;
    }

    public void setLdapSyncFile(String ldapSyncFile)
    {
        this.ldapSyncFile = ldapSyncFile;
    }

    public void setLdapPropertiesFile(String ldapPropertiesFile)
    {
        this.ldapPropertiesFile = ldapPropertiesFile;
    }

    public void setLdapConfigurationTemplatesLocation(String ldapConfigurationTemplatesLocation)
    {
        this.ldapConfigurationTemplatesLocation = ldapConfigurationTemplatesLocation;
    }

    public void setLdapTemplateAuthFile(String ldapTemplateAuthFile)
    {
        this.ldapTemplateAuthFile = ldapTemplateAuthFile;
    }

    public void setLdapTemplateSyncFile(String ldapTemplateSyncFile)
    {
        this.ldapTemplateSyncFile = ldapTemplateSyncFile;
    }

    public void setLdapTemplatePropertiesFile(String ldapTemplatePropertiesFile)
    {
        this.ldapTemplatePropertiesFile = ldapTemplatePropertiesFile;
    }

    public void setLdapPropertiesFileRegex(String ldapPropertiesFileRegex)
    {
        this.ldapPropertiesFileRegex = ldapPropertiesFileRegex;
    }
}
