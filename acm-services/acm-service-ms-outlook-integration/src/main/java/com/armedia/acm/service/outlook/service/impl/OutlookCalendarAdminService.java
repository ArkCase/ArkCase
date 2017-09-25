package com.armedia.acm.service.outlook.service.impl;

import static com.armedia.acm.service.outlook.service.impl.AcmRecreateOutlookFoldersProgressNotifierMessageBuilder.OBJECT_TYPE;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.config.service.CalendarConfigurationExceptionMapper;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;
import com.armedia.acm.calendar.config.service.EmailCredentials;
import com.armedia.acm.calendar.config.service.EmailCredentialsVerifierService;
import com.armedia.acm.calendar.service.CalendarServiceException;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.core.exceptions.AcmOutlookCreateItemFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyEncryptionProperties;
import com.armedia.acm.data.AcmProgressEvent;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.model.AcmOutlookObjectReference;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;
import com.armedia.acm.service.outlook.service.OutlookFolderService;
import com.armedia.acm.service.outlook.service.impl.CalendarFolderHandler.CalendarFolderHandlerCallback;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import microsoft.exchange.webservices.data.core.enumeration.permission.folder.FolderPermissionLevel;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 1, 2017
 *
 */
public class OutlookCalendarAdminService implements OutlookCalendarAdminServiceExtension, ApplicationEventPublisherAware
{
    private static final String USER_ID = "OUTLOOK_CALENDAR_ADMIN_SERVICE";

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    private CalendarAdminService extendedService;

    private UserDao userDao;

    private OutlookFolderService outlookFolderService;

    private List<String> participantsTypesForOutlookFolder;

    private String defaultAccess;

    private String approverAccess;

    private String assigneeAccess;

    private String followerAccess;

    private Map<String, CalendarFolderHandler> folderHandlers;

    private TaskExecutor recreateFoldersExecutor;

    private EmailCredentialsVerifierService verifierService;

    private AcmCryptoUtils cryptoUtils;

    private AcmEncryptablePropertyEncryptionProperties encryptionProperties;

    private AcmOutlookFolderCreatorDao outlookFolderCreatorDao;

    private ApplicationEventPublisher applicationEventPublisher;

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(org.springframework.
     * context.ApplicationEventPublisher)
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.calendar.config.service.CalendarAdminService#readConfiguration(boolean)
     */
    @Override
    public CalendarConfigurationsByObjectType readConfiguration(boolean includePassword) throws CalendarConfigurationException
    {
        return extendedService.readConfiguration(includePassword);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.calendar.config.service.CalendarAdminService#writeConfiguration(com.armedia.acm.calendar.config.
     * service.CalendarConfigurationsByObjectType)
     */
    @Override
    public void writeConfiguration(CalendarConfigurationsByObjectType configuration) throws CalendarConfigurationException
    {
        extendedService.writeConfiguration(configuration);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.calendar.config.service.CalendarAdminService#getExceptionMapper(com.armedia.acm.calendar.config.
     * service.CalendarConfigurationException)
     */
    @Override
    public <CCE extends CalendarConfigurationException> CalendarConfigurationExceptionMapper<CCE> getExceptionMapper(CCE e)
    {
        return extendedService.getExceptionMapper(e);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension#getOutlookUser(java.lang.String)
     */
    @Override
    public Optional<AcmOutlookUser> getEventListenerOutlookUser(String objectType) throws AcmOutlookItemNotFoundException
    {
        try
        {
            return getOutlookUser(null, objectType);
        } catch (CalendarConfigurationException e)
        {
            log.warn("Could not read calendar configuration.", e);
            throw new AcmOutlookItemNotFoundException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension#getOutlookUser(java.lang.String,
     * java.lang.String)
     */
    @Override
    public Optional<AcmOutlookUser> getHandlerOutlookUser(String userName, String objectType) throws PipelineProcessException
    {
        try
        {
            return getOutlookUser(userName, objectType);
        } catch (CalendarConfigurationException e)
        {
            log.warn("Could not read calendar configuration.", e);
            throw new PipelineProcessException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension#recreateFolders(com.armedia.acm.
     * service.outlook.model.AcmOutlookFolderCreator)
     */
    @Override
    public void updateFolderCreatorAndRecreateFoldersIfNecessary(AcmOutlookFolderCreator folderCreator, String userId)
    {
        recreateFoldersExecutor.execute(() -> {

            try
            {
                boolean shouldRecreate = updateFolderCreatorIfNecessary(folderCreator);

                if (shouldRecreate)
                {
                    Set<AcmOutlookObjectReference> objectReferences = outlookFolderCreatorDao.getObjectReferences(folderCreator);

                    AcmRecreateOutlookFolderProgressIndicator progressIndicator = new AcmRecreateOutlookFolderProgressIndicator();
                    progressIndicator.setObjectId(folderCreator.getId());
                    progressIndicator.setObjectType(OBJECT_TYPE);
                    progressIndicator.setUser(userId);
                    progressIndicator.setTotal(objectReferences.size());

                    int updated = 0, failed = 0;

                    for (AcmOutlookObjectReference reference : objectReferences)
                    {
                        try
                        {
                            Optional<AcmOutlookUser> outlookUser = getOutlookUser(null, reference.getObjectType());
                            if (!outlookUser.isPresent())
                            {
                                continue;
                            }

                            CalendarFolderHandler handler = folderHandlers.get(reference.getObjectType());

                            CalendarFolderHandlerCallback callback = (user, objectId, objectType, folderName, container,
                                    participants) -> createFolder(outlookUser.get(), objectId, objectType, folderName, container,
                                            participants);
                            handler.recreateFolder(outlookUser.get(), reference.getObjectId(), reference.getObjectType(), callback);

                            progressIndicator.setProgress(++updated);
                            applicationEventPublisher.publishEvent(new AcmProgressEvent(progressIndicator));

                        } catch (CalendarConfigurationException | CalendarServiceException e)
                        {
                            log.warn("Error while retrieving configured outlook user or recreating outlook folder for [{}] object type.",
                                    reference.getObjectType(), e);
                            progressIndicator.setProgressFailed(++failed);
                            applicationEventPublisher.publishEvent(new AcmProgressEvent(progressIndicator));
                        }
                    }
                }

            } catch (AcmOutlookFolderCreatorDaoException e)
            {
                log.warn("There is no 'AcmOutlookFolderCreator' instance with id [{}] stored in the database. Cannot update it.",
                        folderCreator.getId(), e);
            }

        });
    }

    /**
     * @param folderCreator
     * @return
     * @throws AcmOutlookFolderCreatorDaoException
     */
    @Transactional
    public boolean updateFolderCreatorIfNecessary(AcmOutlookFolderCreator folderCreator) throws AcmOutlookFolderCreatorDaoException
    {
        AcmOutlookFolderCreator existing = outlookFolderCreatorDao.getFolderCreator(folderCreator.getId());

        boolean shouldRecreate = !existing.getSystemEmailAddress().equals(folderCreator.getSystemEmailAddress());
        outlookFolderCreatorDao.updateFolderCreator(existing, folderCreator);
        return shouldRecreate;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createFolder(AcmOutlookUser outlookUser, Long objectId, String objectType, String folderName, AcmContainer container,
            List<AcmParticipant> participants) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException
    {

        OutlookFolder outlookFolder = new OutlookFolder();
        outlookFolder.setDisplayName(folderName);

        List<OutlookFolderPermission> permissions = mapParticipantsToFolderPermission(participants);
        outlookFolder.setPermissions(permissions);
        outlookFolder = outlookFolderService.createFolder(outlookUser, objectId, objectType, WellKnownFolderName.Calendar, outlookFolder);

        container.setCalendarFolderId(outlookFolder.getId());
        container.setCalendarFolderRecreated(true);
    }

    private List<OutlookFolderPermission> mapParticipantsToFolderPermission(List<AcmParticipant> participantsForObject)
    {
        List<OutlookFolderPermission> folderPermissionsToBeAdded = new LinkedList<>();
        if (participantsTypesForOutlookFolder == null || participantsTypesForOutlookFolder.isEmpty())
        {
            // this will cause all permissions in folder to be removed
            log.warn("There are not defined participants types to include");
        } else
        {
            for (AcmParticipant ap : participantsForObject)
            {
                if (participantsTypesForOutlookFolder.contains(ap.getParticipantType()))
                {
                    // add participant to access calendar folder
                    AcmUser user = userDao.findByUserId(ap.getParticipantLdapId());
                    if (user == null)
                    {
                        continue;
                    }
                    OutlookFolderPermission outlookFolderPermission = new OutlookFolderPermission();
                    outlookFolderPermission.setEmail(user.getMail());
                    switch (ap.getParticipantType())
                    {
                    case "follower":
                        if (getFollowerAccess() != null)
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.valueOf(getFollowerAccess()));
                            break;
                        } else
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.PublishingEditor);
                            break;
                        }
                    case "assignee":
                        if (getAssigneeAccess() != null)
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.valueOf(getAssigneeAccess()));
                            break;
                        } else
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.Author);
                            break;
                        }
                    case "approver":
                        if (getApproverAccess() != null)
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.valueOf(getApproverAccess()));
                            break;
                        } else
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.Reviewer);
                            break;
                        }
                    default:
                        if (getDefaultAccess() != null)
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.valueOf(getDefaultAccess()));
                            break;
                        } else
                        {
                            outlookFolderPermission.setLevel(FolderPermissionLevel.None);
                            break;
                        }
                    }
                    folderPermissionsToBeAdded.add(outlookFolderPermission);
                }
            }
        }
        return folderPermissionsToBeAdded;
    }

    /**
     *
     * @see com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension#
     *      getFolderCreatorsWithInvalidCredentials(java.util.List)
     */
    @Override
    public List<AcmOutlookFolderCreator> findFolderCreatorsWithInvalidCredentials()
    {
        List<AcmOutlookFolderCreator> allCreators = outlookFolderCreatorDao.getFolderCreators();

        return allCreators.stream().map(c -> {
            try
            {
                // set decrypted password so it can be used to verify credentials against exchange server
                c.setSystemPassword(decryptValue(c.getSystemPassword()));
                return c;
            } catch (AcmEncryptionException e)
            {
                log.warn(
                        "Error while decrypting password for 'AcmOutlookFolderCreator' instance for user with [{}] system email address. Cannot check its' validity.",
                        c.getSystemEmailAddress(), e);
                return null;
            }
            // filter any potential null values that might be present due to decryption exception, and all instances
            // that doesn't have valid credentials
        }).filter(c -> c != null).filter(c -> !verifierService.verifyEmailCredentials(USER_ID,
                new EmailCredentials(c.getSystemEmailAddress(), c.getSystemPassword()))).map(c -> {
                    // remove the password, we don't want to send decrypted password over the wire back to the user
                    c.setSystemPassword(null);
                    c.setOutlookObjectReferences(null);
                    return c;
                }).collect(Collectors.toList());
    }

    private Optional<AcmOutlookUser> getOutlookUser(String userName, String objectType) throws CalendarConfigurationException
    {
        CalendarConfigurationsByObjectType configurations = extendedService.readConfiguration(true);
        CalendarConfiguration configuration = configurations.getConfiguration(objectType);
        if (configuration != null && configuration.isIntegrationEnabled())
        {
            return Optional.of(new AcmOutlookUser(userName, configuration.getSystemEmail(), configuration.getPassword()));
        }
        return Optional.empty();
    }

    private String decryptValue(String encrypted) throws AcmEncryptionException
    {
        String decryptedValue = new String(cryptoUtils.decryptData(encryptionProperties.getSymmetricKey(), Base64.decodeBase64(encrypted),
                encryptionProperties.getPropertiesEncryptionKeySize(), encryptionProperties.getPropertiesEncryptionIVSize(),
                encryptionProperties.getPropertiesEncryptionMagicSize(), encryptionProperties.getPropertiesEncryptionSaltSize(),
                encryptionProperties.getPropertiesEncryptionPassPhraseIterations(),
                encryptionProperties.getPropertiesEncryptionPassPhraseHashAlgorithm(),
                encryptionProperties.getPropertiesEncryptionAlgorithm(), encryptionProperties.getPropertiesEncryptionBlockCipherMode(),
                encryptionProperties.getPropertiesEncryptionPadding()), UTF8_CHARSET);
        return decryptedValue;
    }

    /**
     * @param extendedService
     *            the extendedService to set
     */
    public void setExtendedService(CalendarAdminService extendedService)
    {
        this.extendedService = extendedService;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    /**
     * @param outlookFolderService
     *            the outlookFolderService to set
     */
    public void setOutlookFolderService(OutlookFolderService outlookFolderService)
    {
        this.outlookFolderService = outlookFolderService;
    }

    /**
     * @param participantsTypesForOutlookFolder
     *            the participantsTypesForOutlookFolder to set
     */
    public void setParticipantsTypesForOutlookFolder(List<String> participantsTypesForOutlookFolder)
    {
        this.participantsTypesForOutlookFolder = participantsTypesForOutlookFolder;
    }

    /**
     * @return the defaultAccess
     */
    public String getDefaultAccess()
    {
        return defaultAccess;
    }

    /**
     * @param defaultAccess
     *            the defaultAccess to set
     */
    public void setDefaultAccess(String defaultAccess)
    {
        this.defaultAccess = defaultAccess;
    }

    /**
     * @return the approverAccess
     */
    public String getApproverAccess()
    {
        return approverAccess;
    }

    /**
     * @param approverAccess
     *            the approverAccess to set
     */
    public void setApproverAccess(String approverAccess)
    {
        this.approverAccess = approverAccess;
    }

    /**
     * @return the assigneeAccess
     */
    public String getAssigneeAccess()
    {
        return assigneeAccess;
    }

    /**
     * @param assigneeAccess
     *            the assigneeAccess to set
     */
    public void setAssigneeAccess(String assigneeAccess)
    {
        this.assigneeAccess = assigneeAccess;
    }

    /**
     * @return the followerAccess
     */
    public String getFollowerAccess()
    {
        return followerAccess;
    }

    /**
     * @param followerAccess
     *            the followerAccess to set
     */
    public void setFollowerAccess(String followerAccess)
    {
        this.followerAccess = followerAccess;
    }

    /**
     * @param folderHandlers
     *            the folderHandlers to set
     */
    public void setFolderHandlers(Map<String, CalendarFolderHandler> folderHandlers)
    {
        this.folderHandlers = folderHandlers;
    }

    /**
     * @param recreateFoldersExecutor
     *            the recreateFoldersExecutor to set
     */
    public void setRecreateFoldersExecutor(TaskExecutor recreateFoldersExecutor)
    {
        this.recreateFoldersExecutor = recreateFoldersExecutor;
    }

    /**
     * @param verifierService
     *            the verifierService to set
     */
    public void setVerifierService(EmailCredentialsVerifierService verifierService)
    {
        this.verifierService = verifierService;
    }

    /**
     * @param cryptoUtils
     *            the cryptoUtils to set
     */
    public void setCryptoUtils(AcmCryptoUtils cryptoUtils)
    {
        this.cryptoUtils = cryptoUtils;
    }

    /**
     * @param encryptionProperties
     *            the encryptionProperties to set
     */
    public void setEncryptionProperties(AcmEncryptablePropertyEncryptionProperties encryptionProperties)
    {
        this.encryptionProperties = encryptionProperties;
    }

    /**
     * @param outlookFolderCreatorDao
     *            the outlookFolderCreatorDao to set
     */
    public void setOutlookFolderCreatorDao(AcmOutlookFolderCreatorDao outlookFolderCreatorDao)
    {
        this.outlookFolderCreatorDao = outlookFolderCreatorDao;
    }

}
