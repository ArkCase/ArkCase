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

import com.armedia.acm.tool.zylab.dao.ZylabMatterCreationDao;
import com.armedia.acm.tool.zylab.exception.ZylabProductionSyncException;
import com.armedia.acm.tool.zylab.model.CreateMatterRequest;
import com.armedia.acm.tool.zylab.model.MatterDTO;
import com.armedia.acm.tool.zylab.model.MatterTemplateDTO;
import com.armedia.acm.tool.zylab.model.ZylabIntegrationConfig;
import com.armedia.acm.tool.zylab.model.ZylabMatterCreationStatus;
import com.armedia.acm.tool.zylab.model.ZylabMatterStatus;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on February, 2021
 */
public class ZylabIntegrationServiceImpl implements ZylabIntegrationService
{

    public static final String DEFAULT_TEMPLATE_IDENTIFIER = "default";
    private transient final Logger log = LogManager.getLogger(getClass());
    private ZylabRestClient zylabRestClient;
    private ZylabIntegrationConfig zylabIntegrationConfig;
    private ZylabMatterCreationDao zylabMatterCreationDao;

    private static boolean isDefaultTemplate(MatterTemplateDTO template)
    {
        return template.getDisplayName() != null && template.getDisplayName().toLowerCase().contains(DEFAULT_TEMPLATE_IDENTIFIER);
    }

    @Override
    public ZylabMatterCreationStatus createMatter(String matterName)
    {
        long defaultMatterTemplateId = getDefaultMatterTemplateId();

        return createMatter(matterName, defaultMatterTemplateId);
    }

    @Override
    public ZylabMatterCreationStatus createMatter(String matterName, long matterTemplateId)
    {
        CreateMatterRequest createMatterRequest = new CreateMatterRequest();
        createMatterRequest.setMatterName(matterName);
        createMatterRequest.setMatterTemplateId(matterTemplateId);

        return createMatter(createMatterRequest);
    }

    @Override
    public ZylabMatterCreationStatus createMatter(CreateMatterRequest createMatterRequest)
    {
        Optional<ZylabMatterCreationStatus> matterStatusOptional = getZylabMatterCreationDao()
                .findByMatterName(createMatterRequest.getMatterName());

        ZylabMatterCreationStatus matterCreationStatus = matterStatusOptional.orElseGet(ZylabMatterCreationStatus::new);

        if (matterStatusOptional.isPresent() && isMatterStatusValid(matterCreationStatus))
        {
            return matterCreationStatus;

        }
        else
        {
            matterCreationStatus.setMatterName(createMatterRequest.getMatterName());
            matterCreationStatus.setStatus(ZylabMatterStatus.IN_PROGRESS);
            matterCreationStatus.setLastUpdated(LocalDateTime.now());
            matterCreationStatus = getZylabMatterCreationDao().save(matterCreationStatus);

            try
            {
                MatterDTO matter = findOrCreateMatter(createMatterRequest);
                matterCreationStatus.setZylabId(matter.getId());
                matterCreationStatus.setStatus(ZylabMatterStatus.CREATED);
            }
            catch (Exception e)
            {
                matterCreationStatus.setStatus(ZylabMatterStatus.FAILED);
            }
            matterCreationStatus.setLastUpdated(LocalDateTime.now());
            getZylabMatterCreationDao().save(matterCreationStatus);
            return matterCreationStatus;
        }
    }

    private boolean isMatterStatusValid(ZylabMatterCreationStatus matterCreationStatus)
    {
        return isMatterCreationInProgressAndActive(matterCreationStatus)
                || matterCreationStatus.getStatus().equals(ZylabMatterStatus.CREATED);
    }

    private boolean isMatterCreationInProgressAndActive(ZylabMatterCreationStatus matterCreationStatus)
    {
        return matterCreationStatus.getStatus().equals(ZylabMatterStatus.IN_PROGRESS)
                && matterCreationStatus.getLastUpdated()
                        .isAfter(LocalDateTime.now().minusMinutes(getZylabIntegrationConfig().getMatterCreationWaitInMinutes()));
    }

    private MatterDTO findOrCreateMatter(CreateMatterRequest createMatterRequest)
    {
        List<MatterDTO> allMatters = getZylabRestClient().getAllMatters();

        // Check if a matter with the same name exists before creating a new one to reduce the chance of duplicate
        // matters
        Optional<MatterDTO> existingMatter = allMatters.stream()
                .filter(matterDTO -> matterDTO.getName().equals(createMatterRequest.getMatterName())).findFirst();

        return existingMatter.orElseGet(() -> getZylabRestClient().createMatter(createMatterRequest));
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
        try
        {
            File zylabProductionFile = getZylabRestClient().getProductionFiles(matterId, productionKey);
            return ZylabProductionFileExtractor.unzip(zylabProductionFile);
        }
        catch (IOException e)
        {
            log.error("Unable to uncompress ZyLAB production files");
            throw new ZylabProductionSyncException("Unable to uncompress ZyLAB production files", e);
        }
        catch (Exception e)
        {
            log.error("Unable to download ZyLAB production files for Production [{}], Matter [{}]", productionKey, matterId);
            throw new ZylabProductionSyncException(
                    "Unable to download ZyLAB production files. Possible wrong production key sent or authentication issues", e);
        }

    }

    @Override
    public void cleanupTemporaryProductionFiles(File tempFolder)
    {
        try
        {
            log.info("Deleting temporary Zylab production folder");
            FileUtils.deleteDirectory(tempFolder);

            String zipFileName = tempFolder.getAbsolutePath().replace("_unzipped", ".zip");
            File zipFile = new File(zipFileName);

            log.info("Deleting original Zylab production zip file");
            Files.deleteIfExists(zipFile.toPath());
        }
        catch (IOException e)
        {
            log.error("Deleting temporary Zylab production files failed");
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

    public ZylabMatterCreationDao getZylabMatterCreationDao()
    {
        return zylabMatterCreationDao;
    }

    public void setZylabMatterCreationDao(ZylabMatterCreationDao zylabMatterCreationDao)
    {
        this.zylabMatterCreationDao = zylabMatterCreationDao;
    }
}
