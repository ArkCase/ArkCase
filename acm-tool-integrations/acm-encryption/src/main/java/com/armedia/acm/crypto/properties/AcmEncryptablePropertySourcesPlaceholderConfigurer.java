package com.armedia.acm.crypto.properties;

/*-
 * #%L
 * Acm Encryption Tools
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

import com.armedia.acm.core.exceptions.AcmEncryptionException;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private final Logger log = LogManager.getLogger(getClass());

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
