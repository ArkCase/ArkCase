package gov.foia.pipeline.postsave;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.foia.model.FOIAPerson;
import gov.foia.model.FOIARequest;
import gov.foia.model.PortalFOIAPerson;

public class FOIARequestPortalUserParticipantsHandler implements PipelineHandler<FOIARequest, CaseFilePipelineContext>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private AcmParticipantService acmParticipantService;

    private UserDao userDao;

    @Override
    public void execute(FOIARequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.debug("Entering FOIARequest portal user participant pipeline handler for object: [{}]", entity);

        FOIAPerson requester = (FOIAPerson) entity.getOriginator().getPerson();

        if (entity.getId() != null && requester instanceof PortalFOIAPerson)
        {

            AcmUser portalUser = getUserDao().findByEmailAddress(requester.getDefaultEmail().getValue()).get(0);

            boolean isPortalUserParticipant = entity.getParticipants().stream()
                    .anyMatch(
                            p -> ParticipantTypes.READER.equals(p.getParticipantType())
                                    && p.getParticipantLdapId().equals(portalUser.getUserId()));

            if (!isPortalUserParticipant)
            {
                AcmParticipant addedParticipant = null;
                try
                {
                    addedParticipant = getAcmParticipantService().saveParticipant(portalUser.getUserId(),
                            ParticipantTypes.READER, entity.getId(), entity.getObjectType());

                    entity.getParticipants().add(addedParticipant);

                    log.debug("Successfully set portal user as participant for case file: [{}]", entity.getId());
                }
                catch (AcmAccessControlException e)
                {
                    log.error("Unable to set portal user as participant for case file: [{}]", entity.getId());
                }
            }
        }

        log.debug("Exiting FOIARequest portal user participant pipeline handler for object: [{}]", entity);
    }

    @Override
    public void rollback(FOIARequest entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {

    }

    public AcmParticipantService getAcmParticipantService()
    {
        return acmParticipantService;
    }

    public void setAcmParticipantService(AcmParticipantService acmParticipantService)
    {
        this.acmParticipantService = acmParticipantService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    // getAcmParticipantService().getParticipantByLdapIdParticipantTypeObjectTypeObjectId(portalUser.getAcmUserId(),
    // ParticipantTypes.READER, entity.getObjectType(), entity.getId()).;
}
