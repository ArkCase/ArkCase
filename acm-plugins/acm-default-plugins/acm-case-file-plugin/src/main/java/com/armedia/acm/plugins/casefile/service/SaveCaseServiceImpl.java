package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.service.outlook.exception.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;
import com.armedia.acm.service.outlook.service.OutlookFolderService;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import microsoft.exchange.webservices.data.enumeration.FolderPermissionLevel;
import microsoft.exchange.webservices.data.enumeration.FolderPermissionReadAccess;
import microsoft.exchange.webservices.data.enumeration.PermissionScope;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 8/29/14.
 */
public class SaveCaseServiceImpl implements SaveCaseService
{
    private CaseFileDao caseFileDao;
    private SaveCaseFileBusinessRule saveRule;
    private CaseFileEventUtility caseFileEventUtility;
    private MuleClient muleClient;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private OutlookService outlookService;
    private OutlookFolderService outlookFolderService;
    private UserDao userDao;

    @Value("#{'${casefile.participants-types-as-outlook-permission}'.trim().replaceAll(\"\\s*(?=,)|(?<=,)\\s*\", \"\").split(',')}")
    private List<String> participantsTypesForOutlookFolder;
    @Value("${casefile.auto_create_calendar_folder}")
    private boolean autoCreateFolderForCaseFile;
    @Value("${casefile.delete_calendar_folder_after_case_closed}")
    private boolean autoDeleteFolderAfterCaseClosed;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    public CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress) throws MuleException
    {
        boolean newCase = in.getId() == null;
        if ( newCase )
        {
            in.setCreator(auth.getName());
        }

        in.setModified(new Date());
        in.setModifier(auth.getName());

        CaseFile retval = getCaseFileDao().save(in);

        log.info("Saving case: retval is null? " + ( retval == null));

        retval = getSaveRule().applyRules(retval);

        // call Mule flow to create the Alfresco folder
        Map<String, Object> messageProps = new HashMap<>();
        messageProps.put("acmUser", auth);
        messageProps.put("auditAdapter", getAuditPropertyEntityAdapter());
        MuleMessage received = getMuleClient().send("vm://saveCaseFile.in", retval, messageProps);
        CaseFile saved = received.getPayload(CaseFile.class);
        MuleException e = received.getInboundProperty("saveException");

        if ( e != null )
        {
            throw e;
        }

        if ( newCase )
        {
            getCaseFileEventUtility().raiseEvent(retval, retval.getStatus(), new Date(), ipAddress, auth.getName(), auth);
        }
        else
        {
        	getCaseFileEventUtility().raiseEvent(retval, "updated", new Date(), ipAddress, auth.getName(), auth);
        }
        //create calendar folder
        if (autoCreateFolderForCaseFile) {
            if (newCase || (saved.getCalendarFolderId() != null && saved.getCalendarFolderId().length() > 0)){
                OutlookFolder folder = new OutlookFolder();
                folder.setDisplayName(in.getTitle() + "(" + saved.getCaseNumber() + ")");
                folder = outlookFolderService.createFolder(WellKnownFolderName.Calendar, folder);
                caseFileDao.insertOutlookFolderId(in.getId(), folder.getId());
                saved.setCalendarFolderId(folder.getId());
            }
            if (saved.getCalendarFolderId() != null && saved.getCalendarFolderId().length() > 0)
                updateFolderParticipants(saved);
        }
        return saved;
    }

    private void updateFolderParticipants(CaseFile caseFile) {
        List<OutlookFolderPermission> folderPermissionsToBeAdded = new LinkedList<>();
        if (participantsTypesForOutlookFolder == null || participantsTypesForOutlookFolder.size() < 1) {
            //this will cause all permissions in folder to be removed
            log.warn("There are not defined participants types to include");
        } else {
            for (AcmParticipant ap : caseFile.getParticipants()) {
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
                    }
                    folderPermissionsToBeAdded.add(outlookFolderPermission);
                }
            }
        }
        outlookFolderService.updateFolderPermissions(caseFile.getCalendarFolderId(), folderPermissionsToBeAdded);
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public SaveCaseFileBusinessRule getSaveRule()
    {
        return saveRule;
    }

    public void setSaveRule(SaveCaseFileBusinessRule saveRule)
    {
        this.saveRule = saveRule;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }

    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public void setOutlookService(OutlookService outlookService) {
        this.outlookService = outlookService;
    }

    public void setOutlookFolderService(OutlookFolderService outlookFolderService) {
        this.outlookFolderService = outlookFolderService;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
