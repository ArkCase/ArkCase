package com.armedia.acm.plugins.casefile.pipeline.postsave;

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

import static com.armedia.acm.auth.AuthenticationUtils.getUserIpAddress;

import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseFileStatusEvent;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStateContants;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class CreateTaskChangeCaseFileHandler
        implements ApplicationEventPublisherAware, PipelineHandler<ChangeCaseStatus, CaseFilePipelineContext>
{
    private ApplicationEventPublisher applicationEventPublisher;
    private EcmFileDao ecmFileDao;

    @Override
    public void execute(ChangeCaseStatus form, CaseFilePipelineContext ctx)
    {
        String mode = (String) ctx.getPropertyValue("mode");
        CaseFile caseFile = ctx.getCaseFile();
        UploadedFiles uploadedFile = new UploadedFiles();

        EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(caseFile.getContainer().getId(),
                caseFile.getContainer().getAttachmentFolder().getId(), ChangeCaseStateContants.CHANGE_CASE_STATUS);

        uploadedFile.setPdfRendition(existing);

        ChangeCaseFileStatusEvent event = new ChangeCaseFileStatusEvent(caseFile.getCaseNumber(), caseFile.getId(), form,
                uploadedFile, mode, ctx.getAuthentication().getName(), getUserIpAddress(), true);
        getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void rollback(ChangeCaseStatus form, CaseFilePipelineContext ctx)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
