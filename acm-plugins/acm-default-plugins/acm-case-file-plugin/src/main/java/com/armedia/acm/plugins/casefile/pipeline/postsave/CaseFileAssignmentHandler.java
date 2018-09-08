package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.service.objecthistory.dao.AcmAssignmentDao;
import com.armedia.acm.service.objecthistory.model.AcmAssignment;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sasko.tanaskoski
 *
 */
public class CaseFileAssignmentHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{

    private AcmAssignmentDao acmAssignmentDao;

    /**
     * Logger instance.
     */
    private transient final Logger log = LoggerFactory.getLogger(getClass());

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
                getAcmAssignmentDao().save(acmAssignment);
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
        try
        {
            AcmAssignment acmAssignment = getAcmAssignmentDao().findByObjectIdAndType(entity.getId(), entity.getObjectType());
            getAcmAssignmentDao().delete(acmAssignment);
        }
        catch (Exception e)
        {
            log.trace("Unable to delete AcmAssignment for objectId [{}] and objectType [{}]", entity.getId(), entity.getObjectType());
            throw new PipelineProcessException(e);
        }
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

}
