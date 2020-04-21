package com.armedia.acm.services.exemption.service.impl;

/*-
 * #%L
 * ACM Service: Exemption
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.exemption.dao.ExemptionCodeDao;
import com.armedia.acm.services.exemption.exception.GetExemptionCodeException;
import com.armedia.acm.services.exemption.exception.SaveExemptionCodeException;
import com.armedia.acm.services.exemption.model.DocumentRedactionEventPublisher;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.exemption.model.ExemptionConstants;
import com.armedia.acm.services.exemption.service.DocumentExemptionService;

/**
 * Created by ana.serafimoska
 */
public class DocumentExemptionServiceImpl implements DocumentExemptionService
{
    private final Logger log = LogManager.getLogger(getClass());
    private EcmFileDao ecmFileDao;
    private ExemptionCodeDao exemptionCodeDao;
    private DocumentRedactionEventPublisher documentRedactionEventPublisher;

    @Override
    @Transactional
    public void updateExemptionCodes(Long fileId, List<String> exemptionCodes, String user) throws SaveExemptionCodeException
    {
        log.info("Saving Exemption codes [{}] from snowbound", exemptionCodes);
        try
        {
            // check if such database record exists
            EcmFile ecmFile = ecmFileDao.find(fileId);
            Objects.requireNonNull(ecmFile, "File not found");

            // select all exemption codes that are not manually added
            List<ExemptionCode> exemptionCodeList = getExemptionCodeDao().getExemptionCodesByFileIdAndVersionTag(fileId,
                    ecmFile.getActiveVersionTag(), exemptionCodes);
            List<String> currentList = exemptionCodeList.stream().map(ExemptionCode::getExemptionCode).collect(Collectors.toList());
            List<String> removeList = new ArrayList<>(currentList);
            removeList.removeAll(exemptionCodes);

            // delete all existing exemption codes that are not in the new exemption codes list associated with given
            // fileId
            getExemptionCodeDao().deleteNotAssociatedExemptionCodeWithGivenFileId(removeList, fileId);
            getDocumentRedactionEventPublisher().publishExemptionCodeDeletedEvent(ecmFile);

            // insert new exemption codes associated with given file id
            exemptionCodes.removeAll(currentList);
            if (!exemptionCodes.isEmpty())
            {
                Date created = new Date();
                for (String exemptionCode : exemptionCodes)
                {
                    ExemptionCode exemptionCodeObject = new ExemptionCode();
                    exemptionCodeObject.setFileId(fileId);
                    exemptionCodeObject.setFileVersion(ecmFile.getActiveVersionTag());
                    exemptionCodeObject.setExemptionCode(exemptionCode);
                    exemptionCodeObject.setCreator(user);
                    exemptionCodeObject.setCreated(created);
                    exemptionCodeObject.setExemptionStatus(ExemptionConstants.EXEMPTION_STATUS_DRAFT);
                    exemptionCodeObject.setParentObjectType("DOCUMENT");
                    exemptionCodeObject.setManuallyFlag(false);
                    getExemptionCodeDao().save(exemptionCodeObject);
                    getDocumentRedactionEventPublisher().publishExemptionCodeCreatedEvent(ecmFile);

                }
            }
        }
        catch (Exception e)
        {
            log.error("Saving Exemption Codes [{}] from snowbound failed", exemptionCodes);
            throw new SaveExemptionCodeException("Unable to save exemption code [{}] from snowbound" + exemptionCodes, e);
        }
    }

    @Override
    @Transactional
    public Integer updateExemptionCodesStatusAfterBurn(Long realFileId, String status, String user) throws SaveExemptionCodeException
    {
        log.info("Updating Exemption codes status to [{}] after burning a document", status, realFileId);
        try
        {
            EcmFile ecmFile = ecmFileDao.find(realFileId);
            Integer executed = getExemptionCodeDao().updateExemptionCodesStatusAfterBurn(realFileId, status, user);
            getDocumentRedactionEventPublisher().publishExemptionCodeCreatedEvent(ecmFile);
            return executed;
        }
        catch (Exception e)
        {
            log.error("Updating Exemption Codes status to [{}] after burning a document failed", status);
            throw new SaveExemptionCodeException("Unable to update exemption code status to [{}] after burning a document" + status,
                    e);
        }
    }

    @Override
    @Transactional
    public List<ExemptionCode> getExemptionCodes(Long caseId, Long fileId) throws GetExemptionCodeException
    {
        log.info("Finding  exemption codes for file: {} associated with objectId: {}", fileId, caseId);
        try
        {
            return getExemptionCodeDao().getExemptionCodesByFileIdAndCaseId(caseId, fileId);
        }
        catch (Exception e)
        {
            log.error("Finding  exemption codes for file: {} associated with objectId: {} failed", fileId, caseId);
            throw new GetExemptionCodeException("Unable to get exemption codes for objectId: {}" + caseId, e);
        }
    }

    @Override
    @Transactional
    public void saveExemptionCodesManually(Long fileId, List<String> exemptionCodes, String user) throws SaveExemptionCodeException
    {
        log.info("Saving Exemption codes [{}] manually", exemptionCodes);
        try
        {
            // check if such database record exists
            EcmFile ecmFile = ecmFileDao.find(fileId);
            if (!exemptionCodes.isEmpty())
            {
                Date created = new Date();
                for (String exemptionCode : exemptionCodes)
                {
                    ExemptionCode exemptionCodeObject = new ExemptionCode();
                    exemptionCodeObject.setFileId(fileId);
                    exemptionCodeObject.setFileVersion(ecmFile.getActiveVersionTag());
                    exemptionCodeObject.setExemptionCode(exemptionCode);
                    exemptionCodeObject.setCreator(user);
                    exemptionCodeObject.setCreated(created);
                    exemptionCodeObject.setExemptionStatus(ExemptionConstants.EXEMPTION_STATUS_MANUAL);
                    exemptionCodeObject.setParentObjectType("DOCUMENT");
                    exemptionCodeObject.setManuallyFlag(true);
                    getExemptionCodeDao().save(exemptionCodeObject);
                    getDocumentRedactionEventPublisher().publishExemptionCodeCreatedEvent(ecmFile);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Saving Exemption Codes [{}] manually failed", exemptionCodes);
            throw new SaveExemptionCodeException("Unable to save exemption code [{}] manually" + exemptionCodes, e);
        }
        log.debug("Updated exemption codes [{}] of document [{}]", exemptionCodes, fileId);

    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public ExemptionCodeDao getExemptionCodeDao()
    {
        return exemptionCodeDao;
    }

    public void setExemptionCodeDao(ExemptionCodeDao exemptionCodeDao)
    {
        this.exemptionCodeDao = exemptionCodeDao;
    }

    public DocumentRedactionEventPublisher getDocumentRedactionEventPublisher()
    {
        return documentRedactionEventPublisher;
    }

    public void setDocumentRedactionEventPublisher(DocumentRedactionEventPublisher documentRedactionEventPublisher)
    {
        this.documentRedactionEventPublisher = documentRedactionEventPublisher;
    }
}
