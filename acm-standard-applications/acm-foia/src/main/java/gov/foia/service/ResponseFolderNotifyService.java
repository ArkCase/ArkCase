package gov.foia.service;

import static gov.foia.model.FOIAConstants.EMAIL_RELEASE_BODY;
import static gov.foia.model.FOIAConstants.EMAIL_RELEASE_SUBJECT;
import static gov.foia.model.FOIAConstants.PORTAL_REQUEST_STATUS_RELATIVE_URL;
import static gov.foia.model.FOIARequestUtils.extractRequestorEmailAddress;

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.dao.UserDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.foia.model.FOIARequest;

/**
 * Created by teng.wang on 3/31/2017.
 */
public class ResponseFolderNotifyService
{
    private CaseFileDao caseFileDao;
    private FolderCompressor compressor;
    private ResponseFolderService responseFolderService;
    private NotificationSender notificationSender;
    private UserDao userDao;
    private Logger log = LoggerFactory.getLogger(getClass());
    private AcmApplication acmAppConfiguration;

    /**
     * @return the acmAppConfiguration
     */
    public AcmApplication getAcmAppConfiguration()
    {
        return acmAppConfiguration;
    }

    /**
     * @param acmAppConfiguration
     *            the acmAppConfiguration to set
     */
    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

    public void sendEmailNotification(Long requestId)
    {
        FOIARequest request = (FOIARequest) caseFileDao.find(requestId);
        String emailAddress = extractRequestorEmailAddress(request.getOriginator().getPerson());
        if (emailAddress != null)
        {
            Notification releaseNotifier = new Notification();
            releaseNotifier.setUserEmail(emailAddress);
            releaseNotifier.setTitle(String.format("%s %s", EMAIL_RELEASE_SUBJECT, request.getCaseNumber()));
            String link = String.format(PORTAL_REQUEST_STATUS_RELATIVE_URL, request.getCaseNumber());
            releaseNotifier.setNote(
                    String.format(EMAIL_RELEASE_BODY, request.getRequestType(), request.getCaseNumber(),
                            getAcmAppConfiguration().getBaseUrl() + link));
            getNotificationSender().send(releaseNotifier);
        }
    }

    public FolderCompressor getCompressor()
    {
        return compressor;
    }

    public void setCompressor(FolderCompressor compressor)
    {
        this.compressor = compressor;
    }

    /**
     * @return the responseFolderService
     */
    public ResponseFolderService getResponseFolderService()
    {
        return responseFolderService;
    }

    /**
     * @param responseFolderService
     *            the responseFolderService to set
     */
    public void setResponseFolderService(ResponseFolderService responseFolderService)
    {
        this.responseFolderService = responseFolderService;
    }

    public NotificationSender getNotificationSender()
    {
        return notificationSender;
    }

    public void setNotificationSender(NotificationSender notificationSender)
    {
        this.notificationSender = notificationSender;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }
}
