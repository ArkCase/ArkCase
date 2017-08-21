package com.armedia.acm.service.outlook.dao.impl;

import com.armedia.acm.calendar.config.service.EmailCredentials;
import com.armedia.acm.calendar.config.service.EmailCredentialsVerifierService;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyEncryptionProperties;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.model.AcmOutlookObjectReference;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 8, 2017
 *
 */
public class JPAAcmOutlookFolderCreatorDao implements AcmOutlookFolderCreatorDao
{

    private static final String USER_ID = "FOLDER_CREATOR_DAO";

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private Logger log = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager em;

    private AcmCryptoUtils cryptoUtils;

    private AcmEncryptablePropertyEncryptionProperties encryptionProperties;

    private EmailCredentialsVerifierService verifierService;

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao#getOutlookObjectReferenct(java.lang.Long,
     * java.lang.String)
     */
    @Override
    public AcmOutlookObjectReference getOutlookObjectReference(Long objectId, String objectType) throws AcmOutlookFolderCreatorDaoException
    {
        log.debug("Retrieving outlook object reference for object with id [{}] of [{}] type.", objectId, objectType);

        TypedQuery<AcmOutlookObjectReference> query = em.createQuery(
                "SELECT oor FROM AcmOutlookObjectReference oor WHERE oor.objectId = :objectId AND oor.objectType = :objectType",
                AcmOutlookObjectReference.class);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        try
        {
            return query.getSingleResult();
        } catch (PersistenceException e)
        {
            log.warn("Error while retrieving 'AcmOutlookObjectReference' instance for objectId [{}] and objectType [{}].", objectId,
                    objectType, e);
            throw new AcmOutlookFolderCreatorDaoException(
                    String.format("Error while retrieving 'AcmOutlookObjectReference' instance for objectId [%s] and objectType [%s].",
                            objectId, objectType),
                    e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao#getFolderCreatorForObject(java.lang.String,
     * java.lang.String)
     */
    @Override
    @Transactional
    public AcmOutlookFolderCreator getFolderCreator(String systemEmailAddress, String systemPassword)
            throws AcmOutlookFolderCreatorDaoException
    {
        log.debug("Retrieving outlook folder creator for system email address [{}].", systemEmailAddress);

        try
        {
            TypedQuery<AcmOutlookFolderCreator> query = em.createQuery(
                    "SELECT ofc FROM AcmOutlookFolderCreator ofc WHERE ofc.systemEmailAddress = :systemEmailAddress",
                    AcmOutlookFolderCreator.class);
            query.setParameter("systemEmailAddress", systemEmailAddress);

            List<AcmOutlookFolderCreator> resultList = query.getResultList();

            if (!resultList.isEmpty())
            {
                log.debug("Outlook folder creator for system email address [{}] retrieved.", systemEmailAddress);
                AcmOutlookFolderCreator outlookFolderCreator = resultList.get(0);
                outlookFolderCreator.setSystemPassword(decryptValue(outlookFolderCreator.getSystemPassword()));
                return outlookFolderCreator;
            } else
            {
                log.debug("Retrieving outlook folder creator for system email address [{}] does not exist, creating one.",
                        systemEmailAddress);
                AcmOutlookFolderCreator folderCreator = new AcmOutlookFolderCreator(systemEmailAddress, encryptValue(systemPassword));
                AcmOutlookFolderCreator outlookFolderCreator = em.merge(folderCreator);
                // the instance must be detached otherwise setting the system password will overwrite the encrypted
                // password value.
                em.detach(outlookFolderCreator);
                outlookFolderCreator.setSystemPassword(systemPassword);
                return outlookFolderCreator;
            }

        } catch (AcmEncryptionException | PersistenceException e)
        {
            log.warn("Error while retrieving 'AcmOutlookFolderCreator' instance for user with [{}] system email address.",
                    systemEmailAddress, e);
            throw new AcmOutlookFolderCreatorDaoException(
                    String.format("Error while retrieving 'AcmOutlookFolderCreator' instance for user with [%s] system email address.",
                            systemEmailAddress),
                    e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao#getFolderCreatorForObject(java.lang.Long,
     * java.lang.String)
     */
    @Override
    public AcmOutlookFolderCreator getFolderCreatorForObject(Long objectId, String objectType) throws AcmOutlookFolderCreatorDaoException
    {
        log.debug("Retrieving outlook folder creator for object with id [{}] of [{}] type.", objectId, objectType);

        TypedQuery<AcmOutlookFolderCreator> query = em.createQuery(
                "SELECT ofc FROM AcmOutlookFolderCreator ofc JOIN ofc.outlookObjectReferences oor WHERE oor.objectId = :objectId AND oor.objectType = :objectType",
                AcmOutlookFolderCreator.class);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        try
        {
            AcmOutlookFolderCreator outlookFolderCreator = query.getSingleResult();
            em.detach(outlookFolderCreator);
            outlookFolderCreator.setSystemPassword(decryptValue(outlookFolderCreator.getSystemPassword()));
            return outlookFolderCreator;
        } catch (PersistenceException | AcmEncryptionException e)
        {
            log.warn(
                    "Error while retrieving 'AcmOutlookFolderCreator' instance associated with the AcmOutlookObjectReference instance with objectId [{}] and objectType [{}].",
                    objectId, objectType, e);
            throw new AcmOutlookFolderCreatorDaoException(String.format(
                    "Error while retrieving 'AcmOutlookFolderCreator' instance associated with the AcmOutlookObjectReference instance with objectId [%s] and objectType [%s].",
                    objectId, objectType), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao#recordFolderCreator(com.armedia.acm.service.
     * outlook.model.AcmOutlookFolderCreator, java.lang.Long, java.lang.String)
     */
    @Override
    @Transactional
    public void recordFolderCreator(AcmOutlookFolderCreator creator, Long objectId, String objectType)
            throws AcmOutlookFolderCreatorDaoException
    {
        log.debug("Storing outlook folder creator for object with id [{}] of [{}] type.", objectId, objectType);

        AcmOutlookObjectReference objectReference = new AcmOutlookObjectReference();
        objectReference.setObjectId(objectId);
        objectReference.setObjectType(objectType);
        try
        {
            creator.setSystemPassword(encryptValue(creator.getSystemPassword()));
        } catch (AcmEncryptionException e)
        {
            log.warn("Error while encrypting password for 'AcmOutlookFolderCreator' instance for user with [{}] system email address.",
                    creator.getSystemEmailAddress(), e);
            throw new AcmOutlookFolderCreatorDaoException(String.format(
                    "Error while encrypting password for 'AcmOutlookFolderCreator' instance for user with [%s] system email address.",
                    creator.getSystemEmailAddress()), e);
        }
        objectReference.setFolderCreator(creator);
        em.merge(objectReference);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao#checkFolderCreatorCredentials()
     */
    @Override
    public List<AcmOutlookFolderCreator> getFolderCreatorsWithInvalidCredentials()
    {
        log.debug("Checking calendar folder creators with invalid credentials.");

        TypedQuery<AcmOutlookFolderCreator> query = em.createQuery("SELECT ofc FROM AcmOutlookFolderCreator ofc",
                AcmOutlookFolderCreator.class);

        List<AcmOutlookFolderCreator> allCreators = query.getResultList();
        allCreators.stream().forEach(c -> em.detach(c));

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

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao#updateFolderCreator(com.armedia.acm.service.
     * outlook.model.AcmOutlookFolderCreator)
     */
    @Override
    @Transactional
    public void updateFolderCreator(AcmOutlookFolderCreator updatedCreator) throws AcmOutlookFolderCreatorDaoException
    {
        log.debug("Updating folder creator with id [{}].", updatedCreator.getId());

        TypedQuery<AcmOutlookFolderCreator> query = em.createQuery("SELECT ofc FROM AcmOutlookFolderCreator ofc WHERE ofc.id = :creatorId",
                AcmOutlookFolderCreator.class);
        query.setParameter("creatorId", updatedCreator.getId());

        try
        {
            AcmOutlookFolderCreator retrievedCreator = query.getSingleResult();
            // TODO: updating system password, should actually trigger folder recreation! Should be addressed with
            // AFDP-4017
            retrievedCreator.setSystemEmailAddress(updatedCreator.getSystemEmailAddress());
            retrievedCreator.setSystemPassword(encryptValue(updatedCreator.getSystemPassword()));
        } catch (AcmEncryptionException e)
        {
            log.warn("Error while encrypting password for 'AcmOutlookFolderCreator' instance for user with id [{}]. Cannot update it.",
                    updatedCreator.getId(), e);
            throw new AcmOutlookFolderCreatorDaoException(String.format(
                    "Error while encrypting password for 'AcmOutlookFolderCreator' instance for user with id [{}]. Cannot update it.",
                    updatedCreator.getId()), e);
        }
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

    private String encryptValue(String plainText) throws AcmEncryptionException
    {
        String encryptedValue = Base64.encodeBase64String(cryptoUtils.encryptData(encryptionProperties.getSymmetricKey(),
                plainText.getBytes(UTF8_CHARSET), encryptionProperties.getPropertiesEncryptionKeySize(),
                encryptionProperties.getPropertiesEncryptionIVSize(), encryptionProperties.getPropertiesEncryptionMagicSize(),
                encryptionProperties.getPropertiesEncryptionSaltSize(), encryptionProperties.getPropertiesEncryptionPassPhraseIterations(),
                encryptionProperties.getPropertiesEncryptionPassPhraseHashAlgorithm(),
                encryptionProperties.getPropertiesEncryptionAlgorithm(), encryptionProperties.getPropertiesEncryptionBlockCipherMode(),
                encryptionProperties.getPropertiesEncryptionPadding()));
        return encryptedValue;
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
     * @param verifierService
     *            the verifierService to set
     */
    public void setVerifierService(EmailCredentialsVerifierService verifierService)
    {
        this.verifierService = verifierService;
    }

}
