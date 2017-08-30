package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.calendar.config.service.CalendarAdminService;
import com.armedia.acm.calendar.config.service.CalendarConfiguration;
import com.armedia.acm.calendar.config.service.CalendarConfigurationException;
import com.armedia.acm.calendar.config.service.CalendarConfigurationExceptionMapper;
import com.armedia.acm.calendar.config.service.CalendarConfigurationsByObjectType;
import com.armedia.acm.calendar.config.service.EmailCredentials;
import com.armedia.acm.calendar.config.service.EmailCredentialsVerifierService;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyEncryptionProperties;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 1, 2017
 *
 */
public class OutlookCalendarAdminService implements OutlookCalendarAdminServiceExtension
{
    private static final String USER_ID = "OUTLOOK_CALENDAR_ADMIN_SERVICE";

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    private CalendarAdminService extendedService;

    private EmailCredentialsVerifierService verifierService;

    private AcmCryptoUtils cryptoUtils;

    private AcmEncryptablePropertyEncryptionProperties encryptionProperties;

    private AcmOutlookFolderCreatorDao outlookFolderCreatorDao;

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
            return Optional.ofNullable(getOutlookUser(null, objectType));
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
            return Optional.ofNullable(getOutlookUser(userName, objectType));
        } catch (CalendarConfigurationException e)
        {
            log.warn("Could not read calendar configuration.", e);
            throw new PipelineProcessException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension#
     * getFolderCreatorsWithInvalidCredentials(java.util.List)
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

    private AcmOutlookUser getOutlookUser(String userName, String objectType) throws CalendarConfigurationException
    {
        CalendarConfigurationsByObjectType configurations = extendedService.readConfiguration(true);
        CalendarConfiguration configuration = configurations.getConfiguration(objectType);
        if (configuration != null && configuration.isIntegrationEnabled())
        {
            return new AcmOutlookUser(userName, configuration.getSystemEmail(), configuration.getPassword());
        }
        return null;
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
