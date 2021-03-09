package com.armedia.acm.tool.zylab.service;

/*-
 * #%L
 * Tool Integrations: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.armedia.acm.tool.zylab.exception.ZylabProductionSyncException;
import com.armedia.acm.tool.zylab.model.CreateMatterRequest;
import com.armedia.acm.tool.zylab.model.MatterDTO;
import com.armedia.acm.tool.zylab.model.MatterTemplateDTO;
import com.armedia.acm.tool.zylab.model.ZylabIntegrationConfig;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on February, 2021
 */
public class ZylabIntegrationServiceImpl implements ZylabIntegrationService
{

    public static final String DEFAULT_TEMPLATE_IDENTIFIER = "default";
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    private transient final Logger log = LogManager.getLogger(getClass());
    private ZylabRestClient zylabRestClient;
    private ZylabIntegrationConfig zylabIntegrationConfig;

    private static boolean isDefaultTemplate(MatterTemplateDTO template)
    {
        return template.getDisplayName() != null && template.getDisplayName().toLowerCase().contains(DEFAULT_TEMPLATE_IDENTIFIER);
    }

    @Override
    public MatterDTO createMatter(String matterName)
    {
        long defaultMatterTemplateId = getDefaultMatterTemplateId();

        return createMatter(matterName, defaultMatterTemplateId);
    }

    @Override
    public MatterDTO createMatter(String matterName, long matterTemplateId)
    {
        CreateMatterRequest createMatterRequest = new CreateMatterRequest();
        createMatterRequest.setMatterName(matterName);
        createMatterRequest.setMatterTemplateId(matterTemplateId);

        return createMatter(createMatterRequest);
    }

    @Override
    public MatterDTO createMatter(CreateMatterRequest createMatterRequest)
    {
        return getZylabRestClient().createMatter(createMatterRequest);
    }

    private long getDefaultMatterTemplateId()
    {
        Long defaultMatterTemplateId = getZylabIntegrationConfig().getDefaultMatterTemplateId();

        if (defaultMatterTemplateId == null)
        {
            List<MatterTemplateDTO> matterTemplates = getZylabRestClient().getMatterTemplates();

            MatterTemplateDTO defaultMatterTemplate = matterTemplates.stream()
                    .filter(ZylabIntegrationServiceImpl::isDefaultTemplate)
                    .findFirst()
                    .orElseGet(() -> matterTemplates.get(0));

            defaultMatterTemplateId = defaultMatterTemplate.getId();
        }

        return defaultMatterTemplateId;
    }

    @Override
    public File getZylabProductionFolder(long matterId, String productionKey) throws ZylabProductionSyncException
    {
        InputStream zylabProductionCompressedFileStream = getZylabRestClient().getProductionFiles(matterId, productionKey);

        String tempFolderName = "Matter_" + matterId + "_Production_" + productionKey;

        return readCompressedFilesToTemporaryFolder(tempFolderName, zylabProductionCompressedFileStream);
    }

    private File readCompressedFilesToTemporaryFolder(String tempFolderName, InputStream zylabProductionCompressedFileStream)
            throws ZylabProductionSyncException
    {
        File tempFolder = new File(tempFolderName);
        boolean tempDirectoryCreated = tempFolder.mkdir();

        if (!tempDirectoryCreated)
        {
            log.error("Unable to create temporary folder for ZyLAB production files");
            throw new ZylabProductionSyncException("Unable to create temporary folder for ZyLAB production files");
        }

        try (BufferedInputStream bis = new BufferedInputStream(zylabProductionCompressedFileStream);
                ZipInputStream zipStream = new ZipInputStream(bis))
        {
            ZipEntry zipEntry;

            while ((zipEntry = zipStream.getNextEntry()) != null)
            {
                readUncompressedContentsToFolder(zipStream, tempFolder, zipEntry.getName());
            }

            log.info("File [{}] successfully uncompressed", tempFolderName);
            return tempFolder;
        }
        catch (IOException e)
        {
            log.error("Unable to uncompress ZyLAB production files");
            throw new ZylabProductionSyncException("Unable to uncompress ZyLAB production files", e);
        }
    }

    private void readUncompressedContentsToFolder(ZipInputStream zipStream, File folder, String fileName) throws IOException
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        File file = new File(folder, fileName);

        try (FileOutputStream fos = new FileOutputStream(file))
        {
            int readProgress;
            while ((readProgress = zipStream.read(buffer)) > 0)
            {
                fos.write(buffer, 0, readProgress);
            }
        }
    }

    @Override
    public void deleteTemporarySyncFolder(File tempFolder) throws ZylabProductionSyncException
    {
        try
        {
            log.info("Deleting temporary Zylab production folder");
            FileUtils.deleteDirectory(tempFolder);
        }
        catch (IOException e)
        {
            log.error("Deleting temporary Zylab production folder failed");
            throw new ZylabProductionSyncException("Deleting temporary Zylab production folder failed", e);
        }
    }

    public ZylabRestClient getZylabRestClient()
    {
        return zylabRestClient;
    }

    public void setZylabRestClient(ZylabRestClient zylabRestClient)
    {
        this.zylabRestClient = zylabRestClient;
    }

    public ZylabIntegrationConfig getZylabIntegrationConfig()
    {
        return zylabIntegrationConfig;
    }

    public void setZylabIntegrationConfig(ZylabIntegrationConfig zylabIntegrationConfig)
    {
        this.zylabIntegrationConfig = zylabIntegrationConfig;
    }

}
