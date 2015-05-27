package com.armedia.acm.plugins.outlook.service.impl;

import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.service.outlook.exception.AcmOutlookCreateItemFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;
import com.armedia.acm.service.outlook.service.OutlookFolderService;
import com.armedia.acm.services.participants.dao.AcmParticipantDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;
import microsoft.exchange.webservices.data.enumeration.FolderPermissionLevel;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nebojsha on 25.05.2015.
 */
public class OutlookContainerCalendarServiceImpl implements OutlookContainerCalendarService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private OutlookFolderService outlookFolderService;
    private AcmContainerDao acmContainerDao;
    private UserDao userDao;

    //properties
    private String systemUserEmail;
    private String systemUserEmailPassword;
    private String systemUserId;
    private List<String> participantsTypesForOutlookFolder;

    @Override
    @Transactional
    public OutlookFolder createFolder(
                                      String folderName,
                                      AcmContainer container,
                                      List<AcmParticipant> participants) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException {

        OutlookFolder outlookFolder = new OutlookFolder();
        outlookFolder.setDisplayName(folderName);

        List<OutlookFolderPermission> permissions = mapParticipantsToFolderPermission(participants);
        outlookFolder.setPermissions(permissions);
        outlookFolder = outlookFolderService.createFolder(getAcmSystemOutlookUser(), WellKnownFolderName.Calendar, outlookFolder);

        container.setCalendarFolderId(outlookFolder.getId());

        return outlookFolder;
    }

    @Override
    @Transactional
    public void deleteFolder(Long containerId,
                             String folderId,
                             DeleteMode deleteMode) throws AcmOutlookItemNotFoundException {
        outlookFolderService.deleteFolder(getAcmSystemOutlookUser(), folderId, deleteMode);
        AcmContainer container = acmContainerDao.find(containerId);
        container.setCalendarFolderId(null);
        acmContainerDao.save(container);
    }

    @Override
    public void updateFolderParticipants(String folderId,
                                         List<AcmParticipant> participants) throws AcmOutlookItemNotFoundException {

        outlookFolderService.updateFolderPermissions(getAcmSystemOutlookUser(), folderId, mapParticipantsToFolderPermission(participants));

    }

    private List<OutlookFolderPermission> mapParticipantsToFolderPermission(List<AcmParticipant> participantsForObject) {
        List<OutlookFolderPermission> folderPermissionsToBeAdded = new LinkedList<>();
        if (participantsTypesForOutlookFolder == null || participantsTypesForOutlookFolder.isEmpty()) {
            //this will cause all permissions in folder to be removed
            log.warn("There are not defined participants types to include");
        } else {
            for (AcmParticipant ap : participantsForObject) {
                if (participantsTypesForOutlookFolder.contains(ap.getParticipantType())) {
                    //add participant to access calendar folder
                    AcmUser user = userDao.findByUserId(ap.getParticipantLdapId());
                    OutlookFolderPermission outlookFolderPermission = new OutlookFolderPermission();
                    outlookFolderPermission.setEmail(user.getMail());
                    switch (ap.getParticipantType()) {
                        case "follower":
                            outlookFolderPermission.setLevel(FolderPermissionLevel.Reviewer);
                            break;
                        case "assignee":
                            outlookFolderPermission.setLevel(FolderPermissionLevel.Author);
                            break;
                        case "approver":
                            outlookFolderPermission.setLevel(FolderPermissionLevel.Reviewer);
                            break;
                        default:
                            outlookFolderPermission.setLevel(FolderPermissionLevel.None);
                            break;
                    }
                    folderPermissionsToBeAdded.add(outlookFolderPermission);
                }
            }
        }
        return folderPermissionsToBeAdded;
    }

    public void setOutlookFolderService(OutlookFolderService outlookFolderService) {
        this.outlookFolderService = outlookFolderService;
    }

    public void setAcmContainerDao(AcmContainerDao acmContainerDao) {
        this.acmContainerDao = acmContainerDao;
    }

    public void setParticipantsTypesForOutlookFolder(String participantsTypesForOutlookFolder) {
        this.participantsTypesForOutlookFolder = !StringUtils.isEmpty(participantsTypesForOutlookFolder) ?
                Arrays.asList(participantsTypesForOutlookFolder.trim().replaceAll(",[\\s]*", ",").split(",")) :
                new ArrayList<>();
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    private AcmOutlookUser getAcmSystemOutlookUser() {
        return new AcmOutlookUser(systemUserId, systemUserEmail, systemUserEmailPassword);
    }

    public void setSystemUserEmail(String systemUserEmail) {
        this.systemUserEmail = systemUserEmail;
    }

    public void setSystemUserEmailPassword(String systemUserEmailPassword) {
        this.systemUserEmailPassword = systemUserEmailPassword;
    }

    public void setSystemUserId(String systemUserId) {
        this.systemUserId = systemUserId;
    }
}
