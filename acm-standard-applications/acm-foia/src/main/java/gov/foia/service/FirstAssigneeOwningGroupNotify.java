package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.services.notification.service.NotificationUtils;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.group.GroupService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class FirstAssigneeOwningGroupNotify extends AbstractEmailSenderService implements ApplicationListener<CaseEvent>
{
    private transient Logger LOG = LoggerFactory.getLogger(getClass());
    private GroupService groupService;
    private SearchResults searchResults;
    private UserDao userDao;
    private NotificationUtils notificationUtils;


    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        if ("com.armedia.acm.casefile.created".equals(event.getEventType().toLowerCase()))
        {
            String assigneeId = ParticipantUtils.getAssigneeIdFromParticipants(event.getCaseFile().getParticipants());
            String owningGroupId = ParticipantUtils.getOwningGroupIdFromParticipants(event.getCaseFile().getParticipants());

           if(Objects.nonNull(owningGroupId))
           {
               try
               {
                   Map<String, String> usersEmails = new HashMap<>();

                   String members = groupService.getUserMembersForGroup(owningGroupId, Optional.empty(), event.getEventUser());
                   JSONArray membersArray = getSearchResults().getDocuments(members);
                   for(int i=0; i< membersArray.length(); i++)
                   {
                       JSONObject memberObject  = membersArray.getJSONObject(i);

                       String userId = getSearchResults().extractString(memberObject, "object_id_s");
                       String emailAddress = getSearchResults().extractString(memberObject, "email_lcs");

                       usersEmails.putIfAbsent(userId, emailAddress);
                   }

                   if(Objects.nonNull(assigneeId))
                   {
                       if(usersEmails.containsKey(assigneeId))
                       {
                           usersEmails.remove(assigneeId);
                       }
                   }

                   List<String> emailAddresses = new ArrayList<>(usersEmails.values());
                   sendAssigneeMailToOwningGroupMembers(event.getCaseFile(), assigneeId, emailAddresses, event.getEventUser());
               }
               catch (MuleException e)
               {
                   LOG.error("Mule error occurred while searching for owning group members", e);
               }
               catch (Exception e)
               {
                   LOG.error("Error occurred while trying to send Assignee email to owning group members", e);;
               }

           }
        }
    }

    private void sendAssigneeMailToOwningGroupMembers(CaseFile caseFile, String assigneeId, List<String> emailAddresses, Authentication authentication) throws Exception {
        AcmUser user = getUserDao().findByUserId(assigneeId);

        String assigneeFullName = user.getFullName();
        String link = getNotificationUtils().buildNotificationLink("CASE_FILE", caseFile.getId(), "CASE_FILE", caseFile.getId());
        String mailSubject = String.format("Request:%s assigned to %s", caseFile.getCaseNumber(),assigneeFullName);
        String mailBody = String.format("Request:%s was assigned to %s Link: %s", caseFile.getCaseNumber(), assigneeFullName, link);

        Map<String, Object> bodyModel = new HashMap<>();

        bodyModel.put("body", mailBody);
        bodyModel.put("header", new String());
        bodyModel.put("footer", new String());

        try
        {
            LOG.info("Trying to send Assignee email to owning group members");
            sendEmailWithAttachment(emailAddresses, mailSubject, bodyModel, null, user, authentication);
        }
        catch (Exception e)
        {
            LOG.error("Could not send the Assignee email to the owning group members", e);
            throw e;
        }
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public NotificationUtils getNotificationUtils()
    {
        return notificationUtils;
    }

    public void setNotificationUtils(NotificationUtils notificationUtils)
    {
        this.notificationUtils = notificationUtils;
    }
}
