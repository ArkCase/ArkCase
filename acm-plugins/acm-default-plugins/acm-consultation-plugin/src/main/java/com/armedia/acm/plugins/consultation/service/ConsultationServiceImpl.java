package com.armedia.acm.plugins.consultation.service;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.consultation.dao.ChangeConsultationStatusDao;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.*;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ConsultationServiceImpl implements ConsultationService
{
    private final Logger log = LogManager.getLogger(getClass());
    private ConsultationDao consultationDao;
    private ChangeConsultationStatusDao changeConsultationStatusDao;
    private PipelineManager<Consultation, ConsultationPipelineContext> pipelineManager;
    private EcmFileService ecmFileService;

    @Override
    public Consultation getConsultationById(long id)
    {
        return getConsultationDao().find(id);
    }

    @Override
    public List<Consultation> getConsultationsByTitle(String title) throws AcmObjectNotFoundException
    {
        return getConsultationDao().findByTitle(title);
    }

    @Override
    public Consultation getConsultationByIdWithChangeStatusIncluded(long id) throws AcmObjectNotFoundException
    {
        try
        {
            Consultation retval = getConsultationDao().find(id);
            if (retval == null)
            {
                throw new PersistenceException("No such consultation with id '" + id + "'");
            }

            ChangeConsultationStatus changeConsultationStatus = getChangeConsultationStatusDao().findByConsultationId(retval.getId());
            retval.setChangeConsultationStatus(changeConsultationStatus);

            return retval;
        }
        catch (PersistenceException e)
        {
            throw new AcmObjectNotFoundException("Consultation", id, e.getMessage(), e);
        }
    }

    @Override
    public Consultation getConsultationByNumber(String consultationNumber)
    {
        return getConsultationDao().findByConsultationNumber(consultationNumber);
    }

    @Override
    public List<ConsultationsByStatusDto> getConsultationsByStatusAndByTimePeriod(TimePeriod numberOfDaysFromToday)
    {
        return consultationDao.getConsultationsByStatusAndByTimePeriod(numberOfDaysFromToday);
    }

    @Override
    public List<Consultation> getNotClosedConsultationsByUser(String user) throws AcmObjectNotFoundException
    {
        return getConsultationDao().getNotClosedConsultationsByUser(user);
    }

    @Override
    @Transactional
    public Consultation saveConsultation(Consultation in, Authentication auth, String ipAddress) throws PipelineProcessException
    {
        return saveConsultation(in, new ArrayList<>(), auth, ipAddress);
    }

    @Override
    @Transactional
    public Consultation saveConsultation(Consultation consultation, Map<String, List<MultipartFile>> filesMap,
            Authentication authentication, String ipAddress)
            throws PipelineProcessException
    {
        List<MultipartFile> files = getFileListFromFilesMap(filesMap);

        return saveConsultation(consultation, files, authentication, ipAddress);
    }

    @Override
    @Transactional
    public Consultation saveConsultation(Consultation consultation, List<MultipartFile> files, Authentication authentication,
            String ipAddress)
            throws PipelineProcessException
    {
        ConsultationPipelineContext pipelineContext = new ConsultationPipelineContext();
        // populate the context
        pipelineContext.setNewConsultation(consultation.getId() == null);
        pipelineContext.setAuthentication(authentication);
        pipelineContext.setIpAddress(ipAddress);

        if (files != null && !files.isEmpty())
        {
            pipelineContext.addProperty("attachmentFiles", files);
        }

        if(consultation.getTitle() == null || consultation.getTitle().length() == 0){
            consultation.setTitle(ConsultationConstants.OBJECT_TYPE);
        }


        return pipelineManager.executeOperation(consultation, pipelineContext, () -> {

            Consultation saved = null;
            try
            {
                saved = consultationDao.save(consultation);
                log.info("Consultation saved '{}'", saved);
            }
            catch (Exception e)
            {
                log.error("Consultation not saved", e);
            }

            return saved;
        });
    }

    private List<MultipartFile> getFileListFromFilesMap(Map<String, List<MultipartFile>> filesMap)
    {
        List<MultipartFile> files = new ArrayList<>();

        if (Objects.nonNull(filesMap))
        {
            for (Map.Entry<String, List<MultipartFile>> file : filesMap.entrySet())
            {
                String fileType = file.getKey();
                file.getValue().stream()
                        .map(item -> new AcmMultipartFile(item, false, fileType))
                        .forEach(files::add);
            }
        }
        return files;
    }

    public ConsultationDao getConsultationDao()
    {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao)
    {
        this.consultationDao = consultationDao;
    }

    public PipelineManager<Consultation, ConsultationPipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<Consultation, ConsultationPipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public ChangeConsultationStatusDao getChangeConsultationStatusDao()
    {
        return changeConsultationStatusDao;
    }

    public void setChangeConsultationStatusDao(ChangeConsultationStatusDao changeConsultationStatusDao)
    {
        this.changeConsultationStatusDao = changeConsultationStatusDao;
    }
}
