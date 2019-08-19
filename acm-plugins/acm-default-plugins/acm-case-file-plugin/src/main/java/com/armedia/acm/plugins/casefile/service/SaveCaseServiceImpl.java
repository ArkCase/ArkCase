package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SaveCaseServiceCaller;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by armdev on 8/29/14.
 */
public class SaveCaseServiceImpl implements SaveCaseService
{
    private final Logger log = LogManager.getLogger(getClass());
    private CaseFileDao caseFileDao;
    private PipelineManager<CaseFile, CaseFilePipelineContext> pipelineManager;
    private EcmFileService ecmFileService;

    @Override
    @Transactional
    public CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress) throws PipelineProcessException
    {
        CaseFile saved = null;
        try
        {
            saved = saveCase(in, new ArrayList<>(), auth, ipAddress);
        }
        catch (AcmUserActionFailedException | AcmCreateObjectFailedException | AcmUpdateObjectFailedException | AcmObjectNotFoundException
                | IOException e)
        {
            log.error("Error in saving Case File");
        }
        return saved;
    }

    @Override
    @Transactional
    public CaseFile saveCase(CaseFile caseFile, List<MultipartFile> files, Authentication authentication, String ipAddress)
            throws AcmUserActionFailedException,
            AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmObjectNotFoundException, PipelineProcessException,
            IOException
    {
        CaseFilePipelineContext pipelineContext = new CaseFilePipelineContext();
        // populate the context
        pipelineContext.setNewCase(caseFile.getId() == null);
        pipelineContext.setAuthentication(authentication);
        pipelineContext.setIpAddress(ipAddress);

        if (files != null && !files.isEmpty())
        {
            pipelineContext.addProperty("attachmentFiles", files);
        }

        return pipelineManager.executeOperation(caseFile, pipelineContext, () -> {

            CaseFile saved = null;
            try
            {
                saved = caseFileDao.save(caseFile);
                log.info("Case saved '{}'", saved);
            }
            catch (Exception e)
            {
                log.error("Case not saved", e);
            }

            return saved;

        });
    }

    @Override
    @Transactional
    public CaseFile saveCase(CaseFile caseFile, Map<String, List<MultipartFile>> filesMap, Authentication authentication, String ipAddress)
            throws PipelineProcessException
    {
        CaseFilePipelineContext pipelineContext = new CaseFilePipelineContext();
        // populate the context
        pipelineContext.setNewCase(caseFile.getId() == null);
        pipelineContext.setAuthentication(authentication);
        pipelineContext.setIpAddress(ipAddress);

        List<AcmMultipartFile> files = new ArrayList<>();

        if(Objects.nonNull(filesMap))
        {
            for (Map.Entry<String, List<MultipartFile>> file : filesMap.entrySet())
            {
                String fileType = file.getKey();
                for (MultipartFile item : file.getValue())
                {
                    try
                    {
                        AcmMultipartFile acmMultipartFile = new AcmMultipartFile(item.getName(), item.getOriginalFilename(),
                                item.getContentType(), item.isEmpty(), item.getSize(), item.getInputStream(), false);
                        acmMultipartFile.setType(fileType);
                        files.add(acmMultipartFile);
                    }
                    catch (IOException e)
                    {
                        log.error("Could not read properties from {} file. Exception {}", item.getOriginalFilename(), e.getMessage());
                    }
                }
            }
        }

        pipelineContext.addProperty("attachmentFiles", files);

        return pipelineManager.executeOperation(caseFile, pipelineContext, () -> {

            CaseFile saved = null;
            try
            {
                saved = caseFileDao.save(caseFile);
                log.info("Case saved '{}'", saved);
            }
            catch (Exception e)
            {
                log.error("Case not saved", e);
            }

            return saved;

        });
    }

    @Override
    public CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress, SaveCaseServiceCaller caller)
            throws PipelineProcessException
    {
        CaseFilePipelineContext pipelineContext = new CaseFilePipelineContext();
        // populate the context
        pipelineContext.setNewCase(in.getId() == null);
        pipelineContext.setAuthentication(auth);
        pipelineContext.setIpAddress(ipAddress);
        pipelineContext.setCaller(caller);

        return pipelineManager.executeOperation(in, pipelineContext, () -> {

            CaseFile saved = caseFileDao.save(in);

            log.info("Case saved '{}'", saved);

            return saved;

        });
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public PipelineManager getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager pipelineManager)
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

}
