package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created by dmiller on 11/7/2016.
 */
public class FindFolderService extends AlfrescoService<Folder>
{
    private EcmFileService ecmFileService;

    private String rmaRootFolder;

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final String BASE_PATH = "/Sites/rm/documentLibrary/";
    private Properties alfrescoRmaPluginProperties;

    /**
     * The context must contain either "objectType" or "folderPath" keys. If "objectType" is present, the category
     * folder path is constructed based on the property "rma_categoryFolder_${objectType}" (this is core ArkCase
     * behavior). Otherwise, the category folder path is constructed by concatenating ${folderPath} onto the
     * RMA base path (this allows extensions to construct the path however they want).
     *
     * @param context
     * @return
     * @throws AlfrescoServiceException
     */
    @Override
    public Folder doService(Map<String, Object> context) throws AlfrescoServiceException
    {
        validateContext(context);

        String fullPath = buildFolderPath(context);

        LOG.debug("Looking for folder {}", fullPath);

        try
        {
            Folder object = (Folder) getEcmFileService().findObjectByPath(fullPath);
            LOG.debug("Found object with id {} for path {}", object.getId(), fullPath);
            return object;

        }
        catch (Exception e)
        {
            throw new AlfrescoServiceException(e.getMessage(), e);
        }
    }

    private String buildFolderPath(Map<String, Object> context)
    {
        if (context.containsKey("folderPath"))
        {
            String path = (String) context.get("folderPath");
            return BASE_PATH + getRmaRootFolder() + "/" + path;
        }

        String objectType = (String) context.get("objectType");

        String propertyName = AlfrescoRmaPluginConstants.CATEGORY_FOLDER_PROPERTY_KEY_PREFIX + objectType;

        String categoryFolderName = getAlfrescoRmaPluginProperties().getProperty(propertyName);

        String fullPath = BASE_PATH + getRmaRootFolder() + "/" + categoryFolderName;

        return fullPath;
    }

    private void validateContext(Map<String, Object> context) throws IllegalArgumentException
    {
        if (context == null || (!context.containsKey("objectType") && !context.containsKey("folderPath")))
        {
            throw new IllegalArgumentException("Context must include either the key 'objectType' or the key 'folderPath'");
        }
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public String getRmaRootFolder()
    {
        return rmaRootFolder;
    }

    public void setRmaRootFolder(String rmaRootFolder)
    {
        this.rmaRootFolder = rmaRootFolder;
    }

    public void setAlfrescoRmaPluginProperties(Properties alfrescoRmaPluginProperties)
    {
        this.alfrescoRmaPluginProperties = alfrescoRmaPluginProperties;
    }

    public Properties getAlfrescoRmaPluginProperties()
    {
        return alfrescoRmaPluginProperties;
    }
}
