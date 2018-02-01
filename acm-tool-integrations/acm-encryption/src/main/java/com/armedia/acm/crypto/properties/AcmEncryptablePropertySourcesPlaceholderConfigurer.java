package com.armedia.acm.crypto.properties;

import com.armedia.acm.core.exceptions.AcmEncryptionException;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;
import java.util.Properties;

/**
 * Subclass of {@link PropertySourcesPlaceholderConfigurer} which decrypts property values if they are encrypted in the
 * loaded resource
 * locations.
 * <p>
 * A value is considered "encrypted" when it appears surrounded by <tt>ENC(...)</tt>, like:
 * <p>
 * <center><tt>my.value=ENC(!"DGAS24FaIO$)</tt></center>
 * <p>
 * Encrypted and unencrypted objects can be combined in the same resources file.
 * </p>
 * Created by Bojan Milenkoski on 19.4.2016
 */
public class AcmEncryptablePropertySourcesPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AcmEncryptablePropertyUtils encryptablePropertyUtils;
    private int propertiesEncryptionKeySize;

    // This flag will keep track of whether the "convertProperties()" method (which decrypts encrypted property entries)
    // has already been
    // called or not.
    private boolean alreadyConverted = false;

    @Override
    protected void convertProperties(final Properties props)
    {
        if (!this.alreadyConverted)
        {
            super.convertProperties(props);
            this.alreadyConverted = true;
        }
    }

    @Override
    protected Properties mergeProperties() throws IOException
    {
        final Properties mergedProperties = super.mergeProperties();
        convertProperties(mergedProperties);
        return mergedProperties;
    }

    @Override
    protected String convertPropertyValue(final String originalValue)
    {
        String decryptedValue = null;
        try
        {
            decryptedValue = encryptablePropertyUtils.decryptPropertyValue(originalValue);
        }
        catch (AcmEncryptionException e)
        {
            throw new RuntimeCryptoException("Failed to convert property value. Reason:" + e.getMessage());
        }
        // log.trace("Decrypted property value: {}", decryptedValue);
        return decryptedValue;
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
}
