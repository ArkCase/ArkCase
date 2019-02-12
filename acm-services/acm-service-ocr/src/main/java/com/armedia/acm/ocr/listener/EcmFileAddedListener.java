package com.armedia.acm.ocr.listener;

/*-
 * #%L
 * acm-ocr
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

import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.ocr.exception.CreateOCRException;
import com.armedia.acm.ocr.model.OCRConstants;
import com.armedia.acm.ocr.model.OCRType;
import com.armedia.acm.ocr.service.ArkCaseOCRService;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileAddedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class EcmFileAddedListener implements ApplicationListener<EcmFileAddedEvent>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private FolderAndFilesUtils folderAndFilesUtils;
    private ArkCaseOCRService arkCaseOCRService;
    private AcmObjectLockingManager objectLockingManager;

    @Override
    public void onApplicationEvent(EcmFileAddedEvent event)
    {
        if (event != null && event.isSucceeded())
        {
            EcmFile file = (EcmFile) event.getSource();
            if(arkCaseOCRService.isExcludedFileTypes(file.getFileType()))
            {
                EcmFileVersion ecmFileVersion = getFolderAndFilesUtils().getVersion(event.getSource(), event.getSource().getActiveVersionTag());

                if (arkCaseOCRService.isOCREnabled() && arkCaseOCRService.isFileVersionOCRable(ecmFileVersion))
                {
                    try
                    {
                        objectLockingManager.acquireObjectLock(ecmFileVersion.getFile().getId(), "FILE", "WRITE", null, true,
                                OCRConstants.OCR_SYSTEM_USER);

                        getArkCaseOCRService().create(ecmFileVersion, OCRType.AUTOMATIC);
                    }
                    catch (CreateOCRException | AcmObjectLockException e)
                    {
                        LOG.error("Creating OCR for MEDIA_FILE_VERSION_ID=[{}] was not executed. REASON=[{}]",
                                ecmFileVersion.getId(), e.getMessage(), e);
                    }
                }
            }
        }
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public ArkCaseOCRService getArkCaseOCRService()
    {
        return arkCaseOCRService;
    }

    public void setArkCaseOCRService(ArkCaseOCRService arkCaseOCRService)
    {
        this.arkCaseOCRService = arkCaseOCRService;
    }

    public AcmObjectLockingManager getObjectLockingManager()
    {
        return objectLockingManager;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }
}
