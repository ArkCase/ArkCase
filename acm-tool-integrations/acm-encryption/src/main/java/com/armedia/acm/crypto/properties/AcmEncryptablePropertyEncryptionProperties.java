package com.armedia.acm.crypto.properties;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Class holding the properties used for encryption/decryption of application properties.
 * <p>
 * Created by Bojan Milenkoski on 22.4.2016
 */
public class AcmEncryptablePropertyEncryptionProperties
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private AcmCryptoUtils cryptoUtils;
    private AcmEncryptablePropertyUtils encryptablePropertyUtils;
    private byte[] symmetricKey;
    private String propertiesEncryptionAlgorithm;
    private String propertiesEncryptionBlockCipherMode;
    private String propertiesEncryptionPadding;
    private int propertiesEncryptionKeySize;
    private int propertiesEncryptionIVSize;
    private int propertiesEncryptionMagicSize;
    private int propertiesEncryptionSaltSize;
    private int propertiesEncryptionPassPhraseIterations;
    private String propertiesEncryptionPassPhraseHashAlgorithm;
    private String encryptedSymmetricKeyEncryptionAlgorithm;
    private String encryptedSymmetricKeyFilePath;
    private String privateKeyEncryptionAlgorithm;
    private String privateKeyFilePath;

    /**
     * @return the symmetricKey
     * @throws AcmEncryptionException
     */
    public byte[] getSymmetricKey() throws AcmEncryptionException
    {
        if (this.symmetricKey == null)
        {
            this.symmetricKey = encryptablePropertyUtils.decryptSymmetricKey();
//            log.debug("Decrypted symmetric key: {}", new String(this.symmetricKey, UTF8_CHARSET));
        }

        return symmetricKey;
    }

    /**
     * @param symmetricKey the symmetricKey to set
     */
    public void setSymmetricKey(byte[] symmetricKey)
    {
        this.symmetricKey = symmetricKey;
    }

    /**
     * @return the propertiesEncryptionAlgorithm
     */
    public String getPropertiesEncryptionAlgorithm()
    {
        return propertiesEncryptionAlgorithm;
    }

    /**
     * @param propertiesEncryptionAlgorithm the propertiesEncryptionAlgorithm to set
     */
    public void setPropertiesEncryptionAlgorithm(String propertiesEncryptionAlgorithm)
    {
        this.propertiesEncryptionAlgorithm = propertiesEncryptionAlgorithm;
    }

    /**
     * @return the propertiesEncryptionBlockCipherMode
     */
    public String getPropertiesEncryptionBlockCipherMode()
    {
        return propertiesEncryptionBlockCipherMode;
    }

    /**
     * @param propertiesEncryptionBlockCipherMode the propertiesEncryptionBlockCipherMode to set
     */
    public void setPropertiesEncryptionBlockCipherMode(String propertiesEncryptionBlockCipherMode)
    {
        this.propertiesEncryptionBlockCipherMode = propertiesEncryptionBlockCipherMode;
    }

    /**
     * @return the propertiesEncryptionPadding
     */
    public String getPropertiesEncryptionPadding()
    {
        return propertiesEncryptionPadding;
    }

    /**
     * @param propertiesEncryptionPadding the propertiesEncryptionPadding to set
     */
    public void setPropertiesEncryptionPadding(String propertiesEncryptionPadding)
    {
        this.propertiesEncryptionPadding = propertiesEncryptionPadding;
    }

    /**
     * @return the propertiesEncryptionKeySize
     */
    public int getPropertiesEncryptionKeySize()
    {
        return propertiesEncryptionKeySize;
    }

    /**
     * @param propertiesEncryptionKeySize the propertiesEncryptionKeySize to set
     */
    public void setPropertiesEncryptionKeySize(int propertiesEncryptionKeySize)
    {
        this.propertiesEncryptionKeySize = propertiesEncryptionKeySize;
    }

    /**
     * @return the propertiesEncryptionIVSize
     */
    public int getPropertiesEncryptionIVSize()
    {
        return propertiesEncryptionIVSize;
    }

    /**
     * @param propertiesEncryptionIVSize the propertiesEncryptionIVSize to set
     */
    public void setPropertiesEncryptionIVSize(int propertiesEncryptionIVSize)
    {
        this.propertiesEncryptionIVSize = propertiesEncryptionIVSize;
    }

    /**
     * @return the propertiesEncryptionMagicSize
     */
    public int getPropertiesEncryptionMagicSize()
    {
        return propertiesEncryptionMagicSize;
    }

    /**
     * @param propertiesEncryptionMagicSize the propertiesEncryptionMagicSize to set
     */
    public void setPropertiesEncryptionMagicSize(int propertiesEncryptionMagicSize)
    {
        this.propertiesEncryptionMagicSize = propertiesEncryptionMagicSize;
    }

    /**
     * @return the propertiesEncryptionSaltSize
     */
    public int getPropertiesEncryptionSaltSize()
    {
        return propertiesEncryptionSaltSize;
    }

    /**
     * @param propertiesEncryptionSaltSize the propertiesEncryptionSaltSize to set
     */
    public void setPropertiesEncryptionSaltSize(int propertiesEncryptionSaltSize)
    {
        this.propertiesEncryptionSaltSize = propertiesEncryptionSaltSize;
    }

    /**
     * @return the propertiesEncryptionPassPhraseIterations
     */
    public int getPropertiesEncryptionPassPhraseIterations()
    {
        return propertiesEncryptionPassPhraseIterations;
    }

    /**
     * @param propertiesEncryptionPassPhraseIterations the propertiesEncryptionPassPhraseIterations to set
     */
    public void setPropertiesEncryptionPassPhraseIterations(int propertiesEncryptionPassPhraseIterations)
    {
        this.propertiesEncryptionPassPhraseIterations = propertiesEncryptionPassPhraseIterations;
    }

    /**
     * @return the propertiesEncryptionPassPhraseHashAlgorithm
     */
    public String getPropertiesEncryptionPassPhraseHashAlgorithm()
    {
        return propertiesEncryptionPassPhraseHashAlgorithm;
    }

    /**
     * @param propertiesEncryptionPassPhraseHashAlgorithm the propertiesEncryptionPassPhraseHashAlgorithm to set
     */
    public void setPropertiesEncryptionPassPhraseHashAlgorithm(String propertiesEncryptionPassPhraseHashAlgorithm)
    {
        this.propertiesEncryptionPassPhraseHashAlgorithm = propertiesEncryptionPassPhraseHashAlgorithm;
    }

    /**
     * @return the encryptedSymmetricKeyEncryptionAlgorithm
     */
    public String getEncryptedSymmetricKeyEncryptionAlgorithm()
    {
        return encryptedSymmetricKeyEncryptionAlgorithm;
    }

    /**
     * @param encryptedSymmetricKeyEncryptionAlgorithm the encryptedSymmetricKeyEncryptionAlgorithm to set
     */
    public void setEncryptedSymmetricKeyEncryptionAlgorithm(String encryptedSymmetricKeyEncryptionAlgorithm)
    {
        this.encryptedSymmetricKeyEncryptionAlgorithm = encryptedSymmetricKeyEncryptionAlgorithm;
    }

    /**
     * @return the privateKeyEncryptionAlgorithm
     */
    public String getPrivateKeyEncryptionAlgorithm()
    {
        return privateKeyEncryptionAlgorithm;
    }

    /**
     * @param privateKeyEncryptionAlgorithm the privateKeyEncryptionAlgorithm to set
     */
    public void setPrivateKeyEncryptionAlgorithm(String privateKeyEncryptionAlgorithm)
    {
        this.privateKeyEncryptionAlgorithm = privateKeyEncryptionAlgorithm;
    }

    /**
     * @return the privateKey
     */
    public String getPrivateKeyFilePath()
    {
        // the file path is relative to "user.home"
        return System.getProperty("user.home") + "/" + privateKeyFilePath;
    }

    /**
     * @param privateKeyFilePath the privateKeyFilePath to set
     */
    public void setPrivateKeyFilePath(String privateKeyFilePath)
    {
        this.privateKeyFilePath = privateKeyFilePath;
    }

    /**
     * @return the encryptedSymmetricKeyFilePath
     */
    public String getEncryptedSymmetricKeyFilePath()
    {
        // the file path is relative to "user.home"
        return System.getProperty("user.home") + "/" + encryptedSymmetricKeyFilePath;
    }

    /**
     * @param encryptedSymmetricKeyFilePath the encryptedSymmetricKeyFilePath to set
     */
    public void setEncryptedSymmetricKeyFilePath(String encryptedSymmetricKeyFilePath)
    {
        this.encryptedSymmetricKeyFilePath = encryptedSymmetricKeyFilePath;
    }

    /**
     * @return the cryptoUtils
     */
    public AcmCryptoUtils getCryptoUtils()
    {
        return cryptoUtils;
    }

    /**
     * @param cryptoUtils the cryptoUtils to set
     */
    public void setCryptoUtils(AcmCryptoUtils cryptoUtils)
    {
        this.cryptoUtils = cryptoUtils;
    }

    /**
     * @return the encryptablePropertyUtils
     */
    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    /**
     * @param encryptablePropertyUtils the encryptablePropertyUtils to set
     */
    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }
}
