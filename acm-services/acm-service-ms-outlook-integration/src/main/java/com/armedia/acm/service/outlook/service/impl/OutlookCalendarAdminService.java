package com.armedia.acm.service.outlook.service.impl;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyEncryptionProperties;
import com.armedia.acm.data.AcmProgressEvent;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.model.AcmOutlookObjectReference;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;
import com.armedia.acm.service.outlook.service.OutlookFolderRecreator;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 1, 2017
 */
public class OutlookCalendarAdminService implements OutlookCalendarAdminServiceExtension, ApplicationEventPublisherAware
{
    private static final String USER_ID = "OUTLOOK_CALENDAR_ADMIN_SERVICE";

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());

    private CalendarAdminService extendedService;

    private TaskExecutor recreateFoldersExecutor;

    private EmailCredentialsVerifierService verifierService;

    private AcmCryptoUtils cryptoUtils;

    private AcmEncryptablePropertyEncryptionProperties encryptionProperties;

    private AcmOutlookFolderCreatorDao outlookFolderCreatorDao;

    private ApplicationEventPublisher applicationEventPublisher;

    private OutlookFolderRecreator folderRecreator;

    /*
     * (non-Javadoc)
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
     * @see com.armedia.acm.calendar.config.service.CalendarAdminService#readConfiguration(boolean)
     */
    @Override
    public CalendarConfigurationsByObjectType readConfiguration(boolean includePassword) throws CalendarConfigurationException
    {
        return extendedService.readConfiguration(includePassword);
    }

    /*
     * (non-Javadoc)
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
     * @see
     * com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension#getOutlookUser(java.lang.String)
     */
    @Override
    public Optional<AcmOutlookUser> getEventListenerOutlookUser(String objectType) throws AcmOutlookItemNotFoundException
    {
        try
        {
            return getOutlookUser(null, objectType);
        }
        catch (CalendarConfigurationException e)
        {
            log.warn("Could not read calendar configuration.", e);
            throw new AcmOutlookItemNotFoundException(e);
        }
    }

    /*
     * (non-Javadoc)
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
        }
        catch (CalendarConfigurationException e)
        {
            log.warn("Could not read calendar configuration.", e);
            throw new PipelineProcessException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension#recreateFolders(com.armedia.acm.
     * service.outlook.model.AcmOutlookFolderCreator)
     */
    @Override
    @Transactional
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

                            folderRecreator.recreateFolder(reference.getObjectType(), reference.getObjectId(), outlookUser.get());

                            progressIndicator.setProgress(++updated);
                            applicationEventPublisher.publishEvent(new AcmProgressEvent(progressIndicator));

                        }
                        catch (CalendarConfigurationException | CalendarServiceException e)
                        {
                            log.warn("Error while retrieving configured outlook user or recreating outlook folder for [{}] object type.",
                                    reference.getObjectType(), e);
                            progressIndicator.setProgressFailed(++failed);
                            applicationEventPublisher.publishEvent(new AcmProgressEvent(progressIndicator));
                        }
                    }
                }

            }
            catch (AcmOutlookFolderCreatorDaoException e)
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

    /**
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
            }
            catch (AcmEncryptionException e)
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

    public void setFolderRecreator(OutlookFolderRecreator folderRecreator)
    {
        this.folderRecreator = folderRecreator;
    }

}
