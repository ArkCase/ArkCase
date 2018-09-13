package com.armedia.acm.services.dataaccess.service;

/*-
 * #%L
 * ACM Service: Data Access Control
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

import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.search.model.solr.SolrBaseDocument;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupServiceImpl;

import org.json.JSONObject;

import javax.persistence.FlushModeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by armdev on 1/14/15.
 */
public class SearchAccessControlFields
{
    private ParticipantAccessChecker participantAccessChecker;
    private UserDao userDao;
    private GroupServiceImpl groupService;

    public void setAccessControlFields(SolrBaseDocument doc, AcmAssignedObject object)
    {
        // all protected objects must have protected_object_b
        doc.setProtected_object_b(true);

        boolean publicDoc = getParticipantAccessChecker().defaultUserHasRead(object);
        doc.setPublic_doc_b(publicDoc);

        if (!publicDoc)
        {
            List<String> readers = getParticipantAccessChecker().getReaders(object);
            Map<String, Long> readerUserIdMap = getParticipantsToUserIdMap(readers);
            doc.setAllow_user_ls(new ArrayList<>(readerUserIdMap.values()));

            readers.removeAll(readerUserIdMap.keySet());
            Map<String, Long> readerGroupIdMap = getParticipantsToGroupIdMap(readers);
            doc.setAllow_group_ls(new ArrayList<>(readerGroupIdMap.values()));
        }

        List<String> denied = getParticipantAccessChecker().getDenied(object);
        Map<String, Long> deniedUserIdMap = getParticipantsToUserIdMap(denied);
        doc.setDeny_user_ls(new ArrayList<>(deniedUserIdMap.values()));

        denied.removeAll(deniedUserIdMap.keySet());
        Map<String, Long> deniedGroupId = getParticipantsToGroupIdMap(denied);
        doc.setDeny_group_ls(new ArrayList<>(deniedGroupId.values()));
    }

    public void setParentAccessControlFields(SolrBaseDocument doc, AcmAssignedObject parentObject)
    {
        List<String> readers = getParticipantAccessChecker().getReaders(parentObject);
        Map<String, Long> readerUserIdMap = getParticipantsToUserIdMap(readers);
        doc.setParent_allow_user_ls(new ArrayList<>(readerUserIdMap.values()));

        readers.removeAll(readerUserIdMap.keySet());
        Map<String, Long> readerGroupIdMap = getParticipantsToGroupIdMap(readers);
        doc.setParent_allow_group_ls(new ArrayList<>(readerGroupIdMap.values()));

        List<String> denied = getParticipantAccessChecker().getDenied(parentObject);
        Map<String, Long> deniedUserIdMap = getParticipantsToUserIdMap(denied);
        doc.setParent_deny_user_ls(new ArrayList<>(deniedUserIdMap.values()));

        denied.removeAll(deniedUserIdMap.keySet());
        Map<String, Long> deniedGroupId = getParticipantsToGroupIdMap(denied);
        doc.setParent_deny_group_ls(new ArrayList<>(deniedGroupId.values()));
    }

    public JSONObject buildParentAccessControlFieldsUpdate(AcmAssignedObject parentObject, String docId)
    {
        JSONObject doc = new JSONObject();
        doc.put("id", docId);

        List<String> readers = getParticipantAccessChecker().getReaders(parentObject);
        Map<String, Long> readerUserIdMap = getParticipantsToUserIdMap(readers);
        JSONObject allowUser = new JSONObject();
        allowUser.put("set", readerUserIdMap.values());
        doc.put("parent_allow_user_ls", allowUser);

        readers.removeAll(readerUserIdMap.keySet());
        Map<String, Long> readerGroupIdMap = getParticipantsToGroupIdMap(readers);
        JSONObject allowGroup = new JSONObject();
        allowGroup.put("set", readerGroupIdMap.values());
        doc.put("parent_allow_group_ls", allowGroup);

        List<String> denied = getParticipantAccessChecker().getDenied(parentObject);
        Map<String, Long> deniedUserIdMap = getParticipantsToUserIdMap(denied);
        JSONObject denyUser = new JSONObject();
        denyUser.put("set", deniedUserIdMap.values());
        doc.put("parent_deny_user_ls", denyUser);

        denied.removeAll(deniedUserIdMap.keySet());
        Map<String, Long> deniedGroupId = getParticipantsToGroupIdMap(denied);
        JSONObject denyGroup = new JSONObject();
        denyGroup.put("set", deniedGroupId.values());
        doc.put("parent_deny_group_ls", denyGroup);
        return doc;
    }

    private Map<String, Long> getParticipantsToUserIdMap(List<String> participantsLdapIds)
    {
        return participantsLdapIds.stream()
                .map(it -> userDao.findByUserId(it))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(AcmUser::getUserId, AcmUser::getIdentifier));
    }

    private Map<String, Long> getParticipantsToGroupIdMap(List<String> participantsLdapIds)
    {
        return participantsLdapIds.stream()
                .map(it -> groupService.findByName(it, FlushModeType.COMMIT))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(AcmGroup::getName, AcmGroup::getIdentifier));
    }

    public ParticipantAccessChecker getParticipantAccessChecker()
    {
        return participantAccessChecker;
    }

    public void setParticipantAccessChecker(ParticipantAccessChecker participantAccessChecker)
    {
        this.participantAccessChecker = participantAccessChecker;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public GroupServiceImpl getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupServiceImpl groupService)
    {
        this.groupService = groupService;
    }
}
