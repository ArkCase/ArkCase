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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author sasko.tanaskoski
 *
 */
public class CaseFileAssignmentHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{

    private AcmAssignmentDao acmAssignmentDao;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;

    /**
     * Logger instance.
     */
    private transient final Logger log = LogManager.getLogger(getClass());

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.pipeline.handler.PipelineHandler#execute(java.lang.Object,
     * com.armedia.acm.services.pipeline.AbstractPipelineContext)
     */
    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.trace("CaseFile entering CaseFileAssignmentHandler : [{}]", entity);
        if (pipelineContext.isNewCase())
        {
            String assigneeId = ParticipantUtils.getAssigneeIdFromParticipants(entity.getParticipants());
            if (assigneeId != null && !assigneeId.equals(""))
            {
                AcmAssignment acmAssignment = createAcmAssignment(entity, assigneeId);
                AcmAssignment saved = getAcmAssignmentDao().save(acmAssignment);
                getAcmObjectHistoryEventPublisher().publishAssigneeChangeEvent(saved, AuthenticationUtils.getUsername(),
                        AuthenticationUtils.getUserIpAddress());
            }
        }
        log.trace("CaseFile exiting CaseFileAssignmentHandler : [{}]", entity);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.pipeline.handler.PipelineHandler#rollback(java.lang.Object,
     * com.armedia.acm.services.pipeline.AbstractPipelineContext)
     */
    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }

    private AcmAssignment createAcmAssignment(CaseFile entity, String assigneeId)
    {
        AcmAssignment assignment = new AcmAssignment();
        assignment.setObjectId(entity.getId());
        assignment.setObjectTitle(entity.getTitle());
        assignment.setObjectName(entity.getCaseNumber());
        assignment.setNewAssignee(assigneeId);
        assignment.setObjectType(CaseFileConstants.OBJECT_TYPE);

        return assignment;
    }

    /**
     * @return the acmAssignmentDao
     */
    public AcmAssignmentDao getAcmAssignmentDao()
    {
        return acmAssignmentDao;
    }

    /**
     * @param acmAssignmentDao
     *            the acmAssignmentDao to set
     */
    public void setAcmAssignmentDao(AcmAssignmentDao acmAssignmentDao)
    {
        this.acmAssignmentDao = acmAssignmentDao;
    }

    /**
     * @return the acmObjectHistoryEventPublisher
     */
    public AcmObjectHistoryEventPublisher getAcmObjectHistoryEventPublisher()
    {
        return acmObjectHistoryEventPublisher;
    }

    /**
     * @param acmObjectHistoryEventPublisher
     *            the acmObjectHistoryEventPublisher to set
     */
    public void setAcmObjectHistoryEventPublisher(AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher)
    {
        this.acmObjectHistoryEventPublisher = acmObjectHistoryEventPublisher;
    }

}
