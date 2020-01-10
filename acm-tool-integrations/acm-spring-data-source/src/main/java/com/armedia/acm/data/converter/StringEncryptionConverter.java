package com.armedia.acm.data.converter;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
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

import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.exceptions.AcmEncryptionBadKeyOrDataException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.io.ByteArrayInputStream;
import java.util.Objects;

/**
 * Created by nebojsha on 05.08.2015.
 */
@Converter
public class StringEncryptionConverter implements AttributeConverter<String, byte[]>
{
    private static AcmCryptoUtils acmCryptoUtils;
    private static Boolean encryptionEnabled;
    private static String encryptionPassphrase;
    private static Boolean databaseEncryptionSupported;
    private final Logger log = LogManager.getLogger(getClass());

    public static void setAcmDecryptionProperties(Boolean encryptionEnabled, AcmCryptoUtils acmCryptoUtils, String encryptionPassphrase,
            Boolean databaseEncryptionSupported)
    {
        StringEncryptionConverter.acmCryptoUtils = acmCryptoUtils;
        StringEncryptionConverter.encryptionPassphrase = encryptionPassphrase;
        StringEncryptionConverter.encryptionEnabled = encryptionEnabled;
        StringEncryptionConverter.databaseEncryptionSupported = databaseEncryptionSupported;
    }

    public static AcmCryptoUtils getAcmCryptoUtils()
    {
        return acmCryptoUtils;
    }

    public static Boolean getEncryptionEnabled()
    {
        return encryptionEnabled;
    }

    public static String getEncryptionPassphrase()
    {
        return encryptionPassphrase;
    }

    public static Boolean getDatabaseEncryptionSupported()
    {
        return databaseEncryptionSupported;
    }

    @Override
    public byte[] convertToDatabaseColumn(String attribute)
    {
        try
        {
            Objects.requireNonNull(databaseEncryptionSupported, "DatabasePlatformSupported must not be null.");
            Objects.requireNonNull(encryptionEnabled, "EncryptionEnabled must not be null.");
            Objects.requireNonNull(encryptionPassphrase, "PassPhrase must not be null.");
            Objects.requireNonNull(acmCryptoUtils, "AcmCryptoUtils must not be null.");
            if (encryptionEnabled)
                return acmCryptoUtils.encryptWithPGP(attribute.getBytes(),
                        encryptionPassphrase.toCharArray(),
                        null,
                        PGPEncryptedDataGenerator.AES_256, // algorithm is hard coded, need some utility to make mapping
                        false);
            else
                return attribute.getBytes();
        }
        catch (AcmEncryptionBadKeyOrDataException e)
        {
            log.error("Error encrypting data.", e);
        }
        return null;
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData)
    {
        try
        {
            Objects.requireNonNull(databaseEncryptionSupported, "DatabasePlatformSupported must not be null.");
            Objects.requireNonNull(encryptionEnabled, "EncryptionEnabled must not be null.");
            Objects.requireNonNull(encryptionPassphrase, "PassPhrase must not be null.");
            Objects.requireNonNull(acmCryptoUtils, "AcmCryptoUtils must not be null.");
            if (encryptionEnabled)
                return new String(
                        acmCryptoUtils.decryptInputStreamWithPGP(new ByteArrayInputStream(dbData), encryptionPassphrase.toCharArray()));
            else
                return new String(dbData);
        }
        catch (AcmEncryptionBadKeyOrDataException e)
        {
            log.error("Error decrypting data.", e);
        }
        return null;
    }
}
