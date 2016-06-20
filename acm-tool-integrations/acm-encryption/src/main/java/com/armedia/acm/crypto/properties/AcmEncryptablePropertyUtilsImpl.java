package com.armedia.acm.crypto.properties;

import com.armedia.acm.core.exceptions.AcmEncryptionException;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Utility class used when encrypting/decrypting values in properties files.
 * <p>
 * A value is considered "encrypted" when it appears surrounded by <tt>ENC(...)</tt>, like:
 * <p>
 * <center><tt>my.value=ENC(!"DGAS24FaIO$)</tt></center>
 * <p>
 * Created by Bojan Milenkoski on 25.4.2016
 */
public class AcmEncryptablePropertyUtilsImpl implements AcmEncryptablePropertyUtils
{

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private final String encryptedValuePrefix = "ENC(";
    private final String encryptedValueSuffix = ")";
    private AcmEncryptablePropertyEncryptionProperties encryptionProperties;

    @Override
    public byte[] decryptSymmetricKey() throws AcmEncryptionException
    {
        // read the encrypted symmetric key
        byte[] encryptedSymmetricKey = null;
        try
        {
            encryptedSymmetricKey = readFileBytes(encryptionProperties.getEncryptedSymmetricKeyFilePath());
        }
        catch (IOException e)
        {
            throw new AcmEncryptionException(
                    "Reading encrypted symmetric key from file: " + encryptionProperties.getEncryptedSymmetricKeyFilePath() + " failed!",
                    e);
        }

        // read private key
        PKCS8EncodedKeySpec keySpec;
        PrivateKey privateKey = null;
        try
        {
            keySpec = new PKCS8EncodedKeySpec(readFileBytes(encryptionProperties.getPrivateKeyFilePath()));
            KeyFactory keyFactory = KeyFactory.getInstance(encryptionProperties.getPrivateKeyEncryptionAlgorithm());
            privateKey = keyFactory.generatePrivate(keySpec);
        }
        catch (IOException e)
        {
            throw new AcmEncryptionException("Failed to decrypt symmetric key. Reading private key from file: "
                    + encryptionProperties.getPrivateKeyFilePath() + " failed!", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new AcmEncryptionException(
                    "Failed to decrypt symmetric key. No such algorithm: " + encryptionProperties.getPrivateKeyEncryptionAlgorithm(), e);
        }
        catch (InvalidKeySpecException e)
        {
            throw new AcmEncryptionException("Failed to decrypt symmetric key. Cannot generate private key!", e);
        }

        // return symmetricKey value. Keep it as byte array NOT as String
        return encryptionProperties.getCryptoUtils().decryptData(privateKey, encryptedSymmetricKey, false,
                encryptionProperties.getEncryptedSymmetricKeyEncryptionAlgorithm());
    }

    @Override
    public String decryptPropertyValue(final String originalValue) throws AcmEncryptionException
    {
        if (!isEncryptedValue(originalValue))
        {
            return originalValue;
        }

        String originalValueEncoded = getInnerEncryptedValue(originalValue.trim());

        String decryptedValue = new String(encryptionProperties.getCryptoUtils().decryptData(encryptionProperties.getSymmetricKey(),
                Base64.decodeBase64(originalValueEncoded), encryptionProperties.getPropertiesEncryptionKeySize(),
                encryptionProperties.getPropertiesEncryptionIVSize(), encryptionProperties.getPropertiesEncryptionMagicSize(),
                encryptionProperties.getPropertiesEncryptionSaltSize(), encryptionProperties.getPropertiesEncryptionPassPhraseIterations(),
                encryptionProperties.getPropertiesEncryptionPassPhraseHashAlgorithm(),
                encryptionProperties.getPropertiesEncryptionAlgorithm(), encryptionProperties.getPropertiesEncryptionBlockCipherMode(),
                encryptionProperties.getPropertiesEncryptionPadding()), UTF8_CHARSET);

        return decryptedValue;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String encryptPropertyValue(String originalValue) throws AcmEncryptionException
    {
        // do not double encrypt values
        if (isEncryptedValue(originalValue))
        {
            return originalValue;
        }

        String encryptedValue = Base64.encodeBase64String(encryptionProperties.getCryptoUtils().encryptData(
                encryptionProperties.getSymmetricKey(), originalValue.getBytes(UTF8_CHARSET),
                encryptionProperties.getPropertiesEncryptionKeySize(), encryptionProperties.getPropertiesEncryptionIVSize(),
                encryptionProperties.getPropertiesEncryptionMagicSize(), encryptionProperties.getPropertiesEncryptionSaltSize(),
                encryptionProperties.getPropertiesEncryptionPassPhraseIterations(),
                encryptionProperties.getPropertiesEncryptionPassPhraseHashAlgorithm(),
                encryptionProperties.getPropertiesEncryptionAlgorithm(), encryptionProperties.getPropertiesEncryptionBlockCipherMode(),
                encryptionProperties.getPropertiesEncryptionPadding()));

        return encryptedValuePrefix + encryptedValue + encryptedValueSuffix;
    }

    private String getInnerEncryptedValue(String value)
    {
        return value.substring(encryptedValuePrefix.length(), (value.length() - encryptedValueSuffix.length()));
    }

    private boolean isEncryptedValue(final String value)
    {
        if (value == null)
        {
            return false;
        }
        final String trimmedValue = value.trim();
        return (trimmedValue.startsWith(encryptedValuePrefix) && trimmedValue.endsWith(encryptedValueSuffix));
    }

    private byte[] readFileBytes(String filename) throws IOException
    {
        Path path = Paths.get(filename);
        return Files.readAllBytes(path);
    }

    /**
     * @return the encryptionProperties
     */
    public AcmEncryptablePropertyEncryptionProperties getEncryptionProperties()
    {
        return encryptionProperties;
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
     * @return the encryptedValuePrefix
     */
    public String getEncryptedValuePrefix()
    {
        return encryptedValuePrefix;
    }

    /**
     * @return the encryptedValueSuffix
     */
    public String getEncryptedValueSuffix()
    {
        return encryptedValueSuffix;
    }
}
