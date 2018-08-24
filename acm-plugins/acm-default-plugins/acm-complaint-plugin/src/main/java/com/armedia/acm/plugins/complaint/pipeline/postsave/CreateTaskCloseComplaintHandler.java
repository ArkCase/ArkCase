package com.armedia.acm.plugins.complaint.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import static com.armedia.acm.plugins.complaint.model.CloseComplaintConstants.CLOSE_COMPLAINT_DOCUMENT;

import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.closeModal.CloseComplaintEvent;
import com.armedia.acm.plugins.complaint.pipeline.CloseComplaintPipelineContext;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class CreateTaskCloseComplaintHandler
        implements ApplicationEventPublisherAware, PipelineHandler<CloseComplaintRequest, CloseComplaintPipelineContext>
{
    private ApplicationEventPublisher applicationEventPublisher;
    private EcmFileDao ecmFileDao;

    @Override
    public void execute(CloseComplaintRequest entity, CloseComplaintPipelineContext ctx)
    {
        String mode = (String) ctx.getPropertyValue("mode");
        Complaint complaint = ctx.getComplaint();
        UploadedFiles uploadedFiles = new UploadedFiles();
        EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(complaint.getContainer().getId(),
                complaint.getContainer().getAttachmentFolder().getId(), CLOSE_COMPLAINT_DOCUMENT);

        uploadedFiles.setPdfRendition(existing);

        CloseComplaintEvent event = new CloseComplaintEvent(complaint.getComplaintNumber(),
                complaint.getComplaintId(),
                entity, uploadedFiles, mode, ctx.getAuthentication().getName(), ctx.getIpAddress(),
                true);
        getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void rollback(CloseComplaintRequest entity, CloseComplaintPipelineContext ctx)
    {
        // nothing to do here, there is no rollback action to be executed
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

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }
}
