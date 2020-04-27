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
import com.armedia.acm.services.email.service.TemplatingEngine;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.helper.UserInfoHelper;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationUtils;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.group.GroupService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FirstAssigneeOwningGroupNotify implements ApplicationListener<CaseEvent>
{
    private transient Logger LOG = LogManager.getLogger(getClass());
    private GroupService groupService;
    private SearchResults searchResults;
    private UserDao userDao;
    private NotificationUtils notificationUtils;
    private String emailBodyTemplate;
    private TemplatingEngine templatingEngine;
    private NotificationDao notificationDao;
    private TranslationService translationService;
    private UserInfoHelper userInfoHelper;

    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        if ("com.armedia.acm.casefile.created".equals(event.getEventType().toLowerCase()))
        {
            String assigneeId = ParticipantUtils.getAssigneeIdFromParticipants(event.getCaseFile().getParticipants());
            String owningGroupId = ParticipantUtils.getOwningGroupIdFromParticipants(event.getCaseFile().getParticipants());

            if (Objects.nonNull(owningGroupId))
            {
                try
                {
                    Map<String, String> usersEmails = new HashMap<>();

                    String members = groupService.getUserMembersForGroup(owningGroupId, Optional.empty(), event.getEventUser());
                    JSONArray membersArray = getSearchResults().getDocuments(members);
                    for (int i = 0; i < membersArray.length(); i++)
                    {
                        JSONObject memberObject = membersArray.getJSONObject(i);

                        String userId = getSearchResults().extractString(memberObject, "object_id_s");
                        String emailAddress = getSearchResults().extractString(memberObject, "email_lcs");

                        usersEmails.putIfAbsent(userId, emailAddress);
                    }

                    if (Objects.nonNull(assigneeId))
                    {
                        if (usersEmails.containsKey(assigneeId))
                        {
                            usersEmails.remove(assigneeId);
                        }
                    }

                    List<String> emailAddresses = new ArrayList<>(usersEmails.values());

                    if (assigneeId != null && !assigneeId.isEmpty())
                    {
                        AcmUser user = getUserDao().findByUserId(assigneeId);

                        String assigneeFullName = user.getFullName();

                        Notification notification = new Notification();
                        notification.setTemplateModelName("requestAssigned");
                        notification.setParentType(event.getObjectType());
                        notification.setParentId(event.getObjectId());
                        notification.setEmailAddresses(emailAddresses.stream().collect(Collectors.joining(",")));
                        notification.setEmailGroup(userInfoHelper.removeGroupPrefix(owningGroupId));
                        notification.setTitle(String.format(translationService.translate(NotificationConstants.REQUEST_ASSIGNED),
                                event.getCaseFile().getCaseNumber(), assigneeFullName));
                        notification.setAttachFiles(false);
                        notification.setUser(user.getUserId());
                        notificationDao.save(notification);
                    }
                }
                catch (SolrException e)
                {
                    LOG.error("Solr error occurred while searching for owning group members", e);
                }
                catch (Exception e)
                {
                    LOG.error("Error occurred while trying to send Assignee email to owning group members", e);
                }

            }
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

    public String getEmailBodyTemplate()
    {
        return emailBodyTemplate;
    }

    public void setEmailBodyTemplate(Resource emailBodyTemplate) throws IOException
    {
        try (DataInputStream resourceStream = new DataInputStream(emailBodyTemplate.getInputStream()))
        {
            byte[] bytes = new byte[resourceStream.available()];
            resourceStream.readFully(bytes);
            this.emailBodyTemplate = new String(bytes, Charset.forName("UTF-8"));
        }
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }

    public UserInfoHelper getUserInfoHelper()
    {
        return userInfoHelper;
    }

    public void setUserInfoHelper(UserInfoHelper userInfoHelper)
    {
        this.userInfoHelper = userInfoHelper;
    }
}
