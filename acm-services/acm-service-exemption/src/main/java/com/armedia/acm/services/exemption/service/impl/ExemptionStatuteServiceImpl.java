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

import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.exemption.dao.ExemptionStatuteDao;
import com.armedia.acm.services.exemption.exception.DeleteExemptionStatuteException;
import com.armedia.acm.services.exemption.exception.GetExemptionStatuteException;
import com.armedia.acm.services.exemption.exception.SaveExemptionStatuteException;
import com.armedia.acm.services.exemption.model.ExemptionCode;
import com.armedia.acm.services.exemption.model.ExemptionCodeAndStatuteEventPublisher;
import com.armedia.acm.services.exemption.model.ExemptionConstants;
import com.armedia.acm.services.exemption.model.ExemptionStatute;
import com.armedia.acm.services.exemption.service.ExemptionStatuteService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public class ExemptionStatuteServiceImpl implements ExemptionStatuteService
{

    private Logger log = LogManager.getLogger(getClass());;
    private ExemptionCodeAndStatuteEventPublisher exemptionCodeAndStatuteEventPublisher;
    private EcmFileDao ecmFileDao;
    private ExemptionStatuteDao exemptionStatuteDao;

    @Override
    public ExemptionStatute saveExemptionStatutes(ExemptionStatute exemptionStatute, String user) throws SaveExemptionStatuteException
    {

        log.info("Saving Exemption statutes [{}]", exemptionStatute.getExemptionStatutes());
        try
        {
            ExemptionStatute exStatute = new ExemptionStatute();
            exStatute.setExemptionStatute(exemptionStatute.getExemptionStatute());
            exStatute.setCreated(new Date());
            exStatute.setCreator(user);
            exStatute.setExemptionStatus(ExemptionConstants.EXEMPTION_STATUS_MANUAL);
            exStatute.setManuallyFlag(true);
            exStatute.setParentObjectId(exemptionStatute.getParentObjectId());
            exStatute.setParentObjectType(exemptionStatute.getParentObjectType());
            ExemptionStatute saved = getExemptionStatuteDao().save(exStatute);
            getExemptionCodeAndStatuteEventPublisher().publishExemptionStatuteCreatedEvent(saved);
            return exStatute;
        }
        catch (Exception e)
        {
            log.error("Saving Exemption Statutes [{}] failed", exemptionStatute.getExemptionStatute());
            throw new SaveExemptionStatuteException("Unable to save exemption statute [{}]" + exemptionStatute.getExemptionStatute(), e);
        }
    }

    @Override
    @Transactional
    public List<ExemptionStatute> getExemptionStatutesOnDocument(Long caseId, Long fileId) throws GetExemptionStatuteException
    {
        log.info("Finding  exemption statutes for file: {} associated with objectId: {}", fileId, caseId);
        try
        {
            return getExemptionStatuteDao().getExemptionStatutesByFileIdAndCaseId(caseId, fileId);
        }
        catch (Exception e)
        {
            log.error("Finding  exemption statutes for file: {} associated with objectId: {} failed", fileId, caseId);
            throw new GetExemptionStatuteException("Unable to get exemption statutes for objectId: {}" + caseId, e);
        }
    }

    @Override
    @Transactional
    public void saveExemptionStatutesOnDocument(Long fileId, List<String> exemptionStatutes, String user)
            throws SaveExemptionStatuteException
    {
        log.info("Saving Exemption statutes [{}] manually", exemptionStatutes);
        try
        {
            // check if such database record exists
            EcmFile ecmFile = ecmFileDao.find(fileId);
            if (!exemptionStatutes.isEmpty())
            {
                for (String exemptionStatute : exemptionStatutes)
                {
                    ExemptionStatute exStatute = new ExemptionStatute();
                    exStatute.setExemptionStatute(exemptionStatute);
                    exStatute.setCreator(user);
                    exStatute.setCreated(new Date());
                    exStatute.setExemptionStatus(ExemptionConstants.EXEMPTION_STATUS_MANUAL);
                    exStatute.setParentObjectType("DOCUMENT");
                    exStatute.setManuallyFlag(true);
                    exStatute.setFileId(fileId);
                    exStatute.setFileVersion(ecmFile.getActiveVersionTag());
                    ExemptionStatute saved = getExemptionStatuteDao().save(exStatute);
                    getExemptionCodeAndStatuteEventPublisher().publishExemptionStatuteCreatedEvent(saved);

                }
            }
        }
        catch (Exception e)
        {
            log.error("Saving Exemption Statutes [{}] manually failed", exemptionStatutes);
            throw new SaveExemptionStatuteException("Unable to save exemption statute [{}] manually" + exemptionStatutes, e);
        }
        log.debug("Updated exemption statutes [{}] of document [{}]", exemptionStatutes, fileId);

    }

    @Override
    public void deleteExemptionStatute(Long statuteId) throws DeleteExemptionStatuteException
    {

        log.info("Deleting exemption statute with id: {}", statuteId);
        try
        {
            ExemptionStatute exemptionStatute = getExemptionStatuteDao().find(statuteId);
            getExemptionStatuteDao().deleteExemptionStatute(statuteId);
            getExemptionCodeAndStatuteEventPublisher().publishExemptionStatuteDeletedEvent(exemptionStatute);
        }
        catch (Exception e)
        {
            log.error("Delete failed for exemption statute with id: {}", statuteId);
            throw new DeleteExemptionStatuteException("Unable to delete exemption statute with id: {}" + statuteId, e);
        }

    }

    @Override
    public void saveExemptionStatutesFromExemptionCodesExecutor(ExemptionCode exemptionCode)
    {
        ExemptionStatute exStatute = new ExemptionStatute();
        exStatute.setExemptionStatute(exemptionCode.getExemptionStatute());
        exStatute.setCreated(exemptionCode.getCreated());
        exStatute.setCreator(exemptionCode.getCreator());
        exStatute.setExemptionStatus(exemptionCode.getExemptionStatus());
        exStatute.setManuallyFlag(exemptionCode.getManuallyFlag());
        exStatute.setParentObjectId(exemptionCode.getParentObjectId());
        exStatute.setParentObjectType(exemptionCode.getParentObjectType());
        exStatute.setFileId(exemptionCode.getFileId());
        exStatute.setFileVersion(exemptionCode.getFileVersion());

        ExemptionStatute saved = getExemptionStatuteDao().save(exStatute);
        getExemptionCodeAndStatuteEventPublisher().publishExemptionStatuteCreatedEvent(saved);
    }

    public ExemptionCodeAndStatuteEventPublisher getExemptionCodeAndStatuteEventPublisher()
    {
        return exemptionCodeAndStatuteEventPublisher;
    }

    public void setExemptionCodeAndStatuteEventPublisher(ExemptionCodeAndStatuteEventPublisher exemptionCodeAndStatuteEventPublisher)
    {
        this.exemptionCodeAndStatuteEventPublisher = exemptionCodeAndStatuteEventPublisher;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public ExemptionStatuteDao getExemptionStatuteDao()
    {
        return exemptionStatuteDao;
    }

    public void setExemptionStatuteDao(ExemptionStatuteDao exemptionStatuteDao)
    {
        this.exemptionStatuteDao = exemptionStatuteDao;
    }
}
