package com.armedia.acm.ocr.listener;

/*-
 * #%L
 * ACM Services: Optical character recognition via Tesseract
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.ocr.exception.GetOCRException;
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRConstants;
import com.armedia.acm.ocr.service.ArkCaseOCRService;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.model.event.EcmFileCopiedEvent;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class EcmFileCopiedListener implements ApplicationListener<EcmFileCopiedEvent>
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private FolderAndFilesUtils folderAndFilesUtils;
    private ArkCaseOCRService arkCaseOCRService;
    private AcmObjectLockingManager objectLockingManager;

    @Override
    public void onApplicationEvent(EcmFileCopiedEvent event)
    {
        if (event != null && event.isSucceeded() && event.getOriginal() != null) {
            EcmFile copy = (EcmFile) event.getSource();
            EcmFile original = event.getOriginal();

            if (!arkCaseOCRService.isExcludedFileTypes(copy.getFileType()))
            {

                // I've saw that we are coping only active version, no other versions for the file, so copy OCR
                // object only for active version
                EcmFileVersion copyActiveVersion = getFolderAndFilesUtils().getVersion(copy, copy.getActiveVersionTag());
                EcmFileVersion originalActiveVersion = getFolderAndFilesUtils().getVersion(original, original.getActiveVersionTag());
                if (originalActiveVersion != null)
                {
                    try
                    {
                        OCR ocr = getArkCaseOCRService().getByMediaVersionId(originalActiveVersion.getId());
                        if (ocr != null)
                        {
                            objectLockingManager.acquireObjectLock(originalActiveVersion.getFile().getId(), "FILE", "WRITE", null, true,
                                    OCRConstants.OCR_SYSTEM_USER);
                            getArkCaseOCRService().copy(ocr, copyActiveVersion);
                        }
                    }
                    catch (GetOCRException | CreateOCRException | AcmObjectLockException e)
                    {
                        LOG.error("Could not copy OCR for EcmFile ID=[{}]. REASON=[{}]", copy.getId(),
                                e.getMessage(), e);
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
