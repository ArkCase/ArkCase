package com.armedia.acm.plugins.casefile.pipeline;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.casefile.service.SaveCaseFileBusinessRule;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.pipeline.PipelineContext;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import org.springframework.security.core.Authentication;

/**
 * Store all the case file saving-related references in this context.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 11.08.2015.
 */
public class CaseFilePipelineContext implements PipelineContext
{
    private SaveCaseFileBusinessRule saveRule;
    private CaseFileEventUtility caseFileEventUtility;
    private MuleContextManager muleContextManager;
    private OutlookService outlookService;
    private OutlookContainerCalendarService outlookContainerCalendarService;
    private UserDao userDao;
    private UserOrgService userOrgService;

    private boolean autoCreateFolderForCaseFile;
    private boolean autoDeleteFolderAfterCaseClosed;

    private AcmFolderService acmFolderService;
    private EcmFileService ecmFileService;
    private String folderStructureAsString;

    /**
     * Flag showing whether new case file is created.
     */
    private boolean newCase;

    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    /**
     * IP Address.
     */
    private String ipAddress;

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

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
    }

    public OutlookService getOutlookService()
    {
        return outlookService;
    }

    public void setOutlookService(OutlookService outlookService)
    {
        this.outlookService = outlookService;
    }

    public OutlookContainerCalendarService getOutlookContainerCalendarService()
    {
        return outlookContainerCalendarService;
    }

    public void setOutlookContainerCalendarService(OutlookContainerCalendarService outlookContainerCalendarService)
    {
        this.outlookContainerCalendarService = outlookContainerCalendarService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public UserOrgService getUserOrgService()
    {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService)
    {
        this.userOrgService = userOrgService;
    }

    public boolean isAutoCreateFolderForCaseFile()
    {
        return autoCreateFolderForCaseFile;
    }

    public void setAutoCreateFolderForCaseFile(boolean autoCreateFolderForCaseFile)
    {
        this.autoCreateFolderForCaseFile = autoCreateFolderForCaseFile;
    }

    public boolean isAutoDeleteFolderAfterCaseClosed()
    {
        return autoDeleteFolderAfterCaseClosed;
    }

    public void setAutoDeleteFolderAfterCaseClosed(boolean autoDeleteFolderAfterCaseClosed)
    {
        this.autoDeleteFolderAfterCaseClosed = autoDeleteFolderAfterCaseClosed;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public String getFolderStructureAsString()
    {
        return folderStructureAsString;
    }

    public void setFolderStructureAsString(String folderStructureAsString)
    {
        this.folderStructureAsString = folderStructureAsString;
    }

    public boolean isNewCase()
    {
        return newCase;
    }

    public void setNewCase(boolean newCase)
    {
        this.newCase = newCase;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }
}
