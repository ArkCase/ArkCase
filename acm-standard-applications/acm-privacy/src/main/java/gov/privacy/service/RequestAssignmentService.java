package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.ParticipantTypes;
import com.armedia.acm.services.participants.service.AcmParticipantService;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpSession;

import java.util.Date;
import java.util.List;

import gov.privacy.dao.SARDao;
import gov.privacy.model.PortalSARPerson;
import gov.privacy.model.SubjectAccessRequest;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class RequestAssignmentService
{
    private SARDao requestDao;
    private SaveCaseService saveCaseService;
    private GroupService groupService;
    private AcmParticipantService acmParticipantService;
    private UserDao userDao;

    private Logger log = LogManager.getLogger(getClass());

    public SubjectAccessRequest startWorking(Long queueId, Authentication auth, HttpSession session)
    {
        SubjectAccessRequest oldestAssignedRequestInQueueToUser = getRequestDao().getOldestRequestInQueueAssignedToUser(queueId, auth.getName());

        // get requests in the current queue with no assignee, sorted by ascending queue due date
        List<SubjectAccessRequest> unassignedRequestsInQueue = getRequestDao().getAllUnassignedRequestsInQueue(queueId);

        SubjectAccessRequest oldestAssignableRequestForUser = getOldestAssignableRequestForUser(unassignedRequestsInQueue, auth.getName());

        if (oldestAssignedRequestInQueueToUser != null && (oldestAssignableRequestForUser == null
                || oldestAssignedRequestInQueueToUser.getQueueEnterDate().isBefore(oldestAssignableRequestForUser.getQueueEnterDate())))
        {
            return oldestAssignedRequestInQueueToUser;
        }

        if (oldestAssignableRequestForUser != null)
        {
            return assignRequestToUser(oldestAssignableRequestForUser, auth, session);
        }

        log.debug("No unassigned requests in queue with ID [[{}]]", queueId);
        return null;
    }

    public SubjectAccessRequest assignRequestToUser(SubjectAccessRequest request, Authentication auth, HttpSession session)
    {
        request.getParticipants().stream().filter(p -> "assignee".equals(p.getParticipantType())).forEach(p -> {
            p.setParticipantLdapId(auth.getName());
        });

        try
        {
            String ipAddress = (String) session.getAttribute("acm_ip_address");
            request.setModifier(AuthenticationUtils.getUsername());
            request.setModified(new Date());
            return (SubjectAccessRequest) getSaveCaseService().saveCase(request, auth, ipAddress);
        }
        catch (Exception e)
        {
            log.error("Unable to assign {} for request [{}]", auth.getName(), request.getId(), e);
        }

        return null;
    }

    private SubjectAccessRequest getOldestAssignableRequestForUser(List<SubjectAccessRequest> unassignedRequestsInQueue, String username)
    {
        for (SubjectAccessRequest request : unassignedRequestsInQueue)
        {
            String owningGroup = ParticipantUtils.getOwningGroupIdFromParticipants(request.getParticipants());
            if (getGroupService().isUserMemberOfGroup(username, owningGroup))
            {
                return request;
            }
        }
        return null;
    }

    public SubjectAccessRequest assignUserGroupToRequest(SubjectAccessRequest request, HttpSession session)

    {
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        List<AcmGroup> groups = groupService.findByUserMember(user);

        request.getParticipants().stream().filter(p -> "owning group".equals(p.getParticipantType())).forEach(p -> {
            p.setParticipantLdapId(groups.get(0).getName());
        });

        return request;
    }

    public void addPortalUserAsParticipantToExistingRequests(AcmUser user, PortalSARPerson requester)
    {

        List<SubjectAccessRequest> requestsByPerson = requestDao.getAllRequestsByRequester(requester.getId());

        for (SubjectAccessRequest request : requestsByPerson)
        {
            if (request.getId() != null && requester != null)
            {

                AcmUser portalUser = getUserDao().findByEmailAddress(requester.getDefaultEmail().getValue()).get(0);

                boolean isPortalUserParticipant = request.getParticipants().stream()
                        .anyMatch(
                                p -> ParticipantTypes.READER.equals(p.getParticipantType())
                                        && p.getParticipantLdapId().equals(portalUser.getUserId()));

                if (!isPortalUserParticipant)
                {
                    AcmParticipant addedParticipant = null;
                    try
                    {
                        addedParticipant = getAcmParticipantService().saveParticipant(portalUser.getUserId(),
                                ParticipantTypes.READER, request.getId(), request.getObjectType());

                        request.getParticipants().add(addedParticipant);

                        log.debug("Successfully set portal user as participant for case file: [{}]", request.getId());
                    }
                    catch (AcmAccessControlException e)
                    {
                        log.error("Unable to set portal user as participant for case file: [{}]", request.getId());
                    }
                }

            }
        }

    }

    public SARDao getRequestDao()
    {
        return requestDao;
    }

    public void setRequestDao(SARDao requestDao)
    {
        this.requestDao = requestDao;
    }

    public SaveCaseService getSaveCaseService()
    {
        return saveCaseService;
    }

    public void setSaveCaseService(SaveCaseService saveCaseService)
    {
        this.saveCaseService = saveCaseService;
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
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
}
