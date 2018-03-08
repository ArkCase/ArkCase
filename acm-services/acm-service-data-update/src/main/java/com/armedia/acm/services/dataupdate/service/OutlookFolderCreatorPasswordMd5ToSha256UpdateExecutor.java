package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyEncryptionProperties;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.google.common.base.Preconditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Base64;
import java.util.List;

public class OutlookFolderCreatorPasswordMd5ToSha256UpdateExecutor implements AcmDataUpdateExecutor
{

    private final transient Logger log = LoggerFactory.getLogger(getClass());

    private AcmCryptoUtils cryptoUtils;
    private AcmEncryptablePropertyEncryptionProperties cryptoProperties;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String getUpdateId()
    {
        return "AFDP-5558-update-outlook-creator-password-for-sha256";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute()
    {
        try
        {
            verifyPreconditions();
            updateOutlookFolderCreatorPasswords();
        }
        catch (IllegalStateException | NullPointerException e)
        {
            log.error("{} cannot be run - {}", getUpdateId(), e.getMessage());
        }

    }

    public void updateOutlookFolderCreatorPasswords()
    {
        List<AcmOutlookFolderCreator> folderCreators = getEntityManager()
                .createQuery("SELECT e FROM AcmOutlookFolderCreator e", AcmOutlookFolderCreator.class).getResultList();

        log.info("{} folder creators to check", folderCreators.size());

        for (AcmOutlookFolderCreator folderCreator : folderCreators)
        {
            log.debug("checking folder creator {}", folderCreator.getSystemEmailAddress());

            // can we decrypt with md5 hash? If so, we need to re-encrypt it with SHA256
            try
            {
                byte[] decrypted = getCryptoUtils().decryptData(
                        getCryptoProperties().getSymmetricKey(),
                        Base64.getDecoder().decode(folderCreator.getSystemPassword()),
                        getCryptoProperties().getPropertiesEncryptionKeySize(),
                        getCryptoProperties().getPropertiesEncryptionIVSize(),
                        getCryptoProperties().getPropertiesEncryptionMagicSize(),
                        getCryptoProperties().getPropertiesEncryptionSaltSize(),
                        getCryptoProperties().getPropertiesEncryptionPassPhraseIterations(),
                        "MD5",
                        getCryptoProperties().getPropertiesEncryptionAlgorithm(),
                        getCryptoProperties().getPropertiesEncryptionBlockCipherMode(),
                        getCryptoProperties().getPropertiesEncryptionPadding());
                log.debug("{} password is MD5-hashed, proceeding with update", folderCreator.getSystemEmailAddress());

                byte[] encrypted = getCryptoUtils().encryptData(
                        getCryptoProperties().getSymmetricKey(),
                        decrypted,
                        getCryptoProperties().getPropertiesEncryptionKeySize(),
                        getCryptoProperties().getPropertiesEncryptionIVSize(),
                        getCryptoProperties().getPropertiesEncryptionMagicSize(),
                        getCryptoProperties().getPropertiesEncryptionSaltSize(),
                        getCryptoProperties().getPropertiesEncryptionPassPhraseIterations(),
                        getCryptoProperties().getPropertiesEncryptionPassPhraseHashAlgorithm(),
                        getCryptoProperties().getPropertiesEncryptionAlgorithm(),
                        getCryptoProperties().getPropertiesEncryptionBlockCipherMode(),
                        getCryptoProperties().getPropertiesEncryptionPadding());
                String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
                folderCreator.setSystemPassword(encryptedBase64);
                getEntityManager().merge(folderCreator);
            }
            catch (AcmEncryptionException ace)
            {
                log.debug("{} password is not MD5-hashed, no need to update it", folderCreator.getSystemEmailAddress());
            }
        }

        log.info("Done checking {} folder creators", folderCreators.size());

    }

    private void verifyPreconditions() throws IllegalStateException, NullPointerException
    {
        Preconditions.checkNotNull(getCryptoUtils(), "Encryption utilities are not set");
        Preconditions.checkNotNull(getCryptoProperties(), "Encryption properties are not set");
        Preconditions.checkNotNull(getEntityManager(), "Entity manager is not set");
        Preconditions.checkState("SHA256".equals(getCryptoProperties().getPropertiesEncryptionPassPhraseHashAlgorithm()),
                "Encryption properties have not been updated to SHA256");

    }

    public AcmCryptoUtils getCryptoUtils()
    {
        return cryptoUtils;
    }

    public void setCryptoUtils(AcmCryptoUtils cryptoUtils)
    {
        this.cryptoUtils = cryptoUtils;
    }

    public AcmEncryptablePropertyEncryptionProperties getCryptoProperties()
    {
        return cryptoProperties;
    }

    public void setCryptoProperties(AcmEncryptablePropertyEncryptionProperties cryptoProperties)
    {
        this.cryptoProperties = cryptoProperties;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
}
