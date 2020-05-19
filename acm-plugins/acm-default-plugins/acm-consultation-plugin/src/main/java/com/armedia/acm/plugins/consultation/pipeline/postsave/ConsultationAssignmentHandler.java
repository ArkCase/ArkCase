package com.armedia.acm.plugins.consultation.pipeline.postsave;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.service.objecthistory.service.AcmObjectHistoryEventPublisher;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConsultationAssignmentHandler implements PipelineHandler<Consultation, ConsultationPipelineContext>
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
    public void execute(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.trace("Consultation entering ConsultationAssignmentHandler : [{}]", entity);
        if (pipelineContext.isNewConsultation())
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
        log.trace("Consultation exiting ConsultationAssignmentHandler : [{}]", entity);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.pipeline.handler.PipelineHandler#rollback(java.lang.Object,
     * com.armedia.acm.services.pipeline.AbstractPipelineContext)
     */
    @Override
    public void rollback(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }

    private AcmAssignment createAcmAssignment(Consultation entity, String assigneeId)
    {
        AcmAssignment assignment = new AcmAssignment();
        assignment.setObjectId(entity.getId());
        assignment.setObjectTitle(entity.getTitle());
        assignment.setObjectName(entity.getConsultationNumber());
        assignment.setNewAssignee(assigneeId);
        assignment.setObjectType(ConsultationConstants.OBJECT_TYPE);

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
