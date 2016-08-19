package com.armedia.acm.crypto.properties;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.AcmCryptoUtils;

/**
 * Class holding the properties used for encryption/decryption of application properties.
 * <p>
 * Created by Bojan Milenkoski on 22.4.2016
 */
public class AcmEncryptablePropertyEncryptionProperties
{
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
    private String keystoreType;
    private String keystorePath;
    private String keystorePassword;
    private String privateKeyAlias;

    /**
     * @return the symmetricKey
     * @throws AcmEncryptionException
     */
    public byte[] getSymmetricKey() throws AcmEncryptionException
    {
        if (this.symmetricKey == null)
        {
            this.symmetricKey = encryptablePropertyUtils.decryptSymmetricKey();
        }

        return symmetricKey;
    }

    /**
     * @param symmetricKey
     *            the symmetricKey to set
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
     * @param propertiesEncryptionAlgorithm
     *            the propertiesEncryptionAlgorithm to set
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
     * @param propertiesEncryptionBlockCipherMode
     *            the propertiesEncryptionBlockCipherMode to set
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
     * @param propertiesEncryptionPadding
     *            the propertiesEncryptionPadding to set
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
     * @param propertiesEncryptionKeySize
     *            the propertiesEncryptionKeySize to set
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
     * @param propertiesEncryptionIVSize
     *            the propertiesEncryptionIVSize to set
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
     * @param propertiesEncryptionMagicSize
     *            the propertiesEncryptionMagicSize to set
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
     * @param propertiesEncryptionSaltSize
     *            the propertiesEncryptionSaltSize to set
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
     * @param propertiesEncryptionPassPhraseIterations
     *            the propertiesEncryptionPassPhraseIterations to set
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
     * @param propertiesEncryptionPassPhraseHashAlgorithm
     *            the propertiesEncryptionPassPhraseHashAlgorithm to set
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
     * @param encryptedSymmetricKeyEncryptionAlgorithm
     *            the encryptedSymmetricKeyEncryptionAlgorithm to set
     */
    public void setEncryptedSymmetricKeyEncryptionAlgorithm(String encryptedSymmetricKeyEncryptionAlgorithm)
    {
        this.encryptedSymmetricKeyEncryptionAlgorithm = encryptedSymmetricKeyEncryptionAlgorithm;
    }

    /**
     * @return the encryptedSymmetricKeyFilePath
     */
    public String getEncryptedSymmetricKeyFilePath()
    {
        return encryptedSymmetricKeyFilePath;
    }

    /**
     * @param encryptedSymmetricKeyFilePath
     *            the encryptedSymmetricKeyFilePath to set
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
     * @param cryptoUtils
     *            the cryptoUtils to set
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
     * @param encryptablePropertyUtils
     *            the encryptablePropertyUtils to set
     */
    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }

    /**
     * @return the keystoreType
     */
    public String getKeystoreType()
    {
        return keystoreType;
    }

    /**
     * @param keystoreType
     *            the keystoreType to set
     */
    public void setKeystoreType(String keystoreType)
    {
        this.keystoreType = keystoreType;
    }

    /**
     * @return the keystorePath
     */
    public String getKeystorePath()
    {
        return keystorePath;
    }

    /**
     * @param keystorePath
     *            the keystorePath to set
     */
    public void setKeystorePath(String keystorePath)
    {
        this.keystorePath = keystorePath;
    }

    /**
     * @return the keystorePassword
     */
    public String getKeystorePassword()
    {
        return keystorePassword;
    }

    /**
     * @param keystorePassword
     *            the keystorePassword to set
     */
    public void setKeystorePassword(String keystorePassword)
    {
        this.keystorePassword = keystorePassword;
    }

    /**
     * @return the privateKeyAlias
     */
    public String getPrivateKeyAlias()
    {
        return privateKeyAlias;
    }

    /**
     * @param privateKeyAlias
     *            the privateKeyAlias to set
     */
    public void setPrivateKeyAlias(String privateKeyAlias)
    {
        this.privateKeyAlias = privateKeyAlias;
    }
}
