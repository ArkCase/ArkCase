package com.armedia.acm.data.converter;

import com.armedia.acm.core.exceptions.AcmEncryptionBadKeyOrDataException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.ByteArrayInputStream;
import java.util.Objects;

/**
 * Created by nebojsha on 05.08.2015.
 */
@Converter
public class StringEncryptionConverter implements AttributeConverter<String, byte[]> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static AcmCryptoUtils acmCryptoUtils;

    private static Boolean encryptionEnabled;
    private static String encryptionPassphrase;
    private static Boolean databaseEncryptionSupported;

    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
        try {
            Objects.requireNonNull(databaseEncryptionSupported, "DatabasePlatformSupported must not be null.");
            Objects.requireNonNull(encryptionEnabled, "EncryptionEnabled must not be null.");
            Objects.requireNonNull(encryptionPassphrase, "PassPhrase must not be null.");
            Objects.requireNonNull(acmCryptoUtils, "AcmCryptoUtils must not be null.");
            if (encryptionEnabled)
                return acmCryptoUtils.encryptWithPGP(attribute.getBytes(),
                        encryptionPassphrase.toCharArray(),
                        null,
                        PGPEncryptedDataGenerator.AES_256, //algorithm is hard coded, need some utility to make mapping
                        false);
            else return attribute.getBytes();
        } catch (AcmEncryptionBadKeyOrDataException e) {
            log.error("Error encrypting data.", e);
        }
        return null;
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData) {
        try {
            Objects.requireNonNull(databaseEncryptionSupported, "DatabasePlatformSupported must not be null.");
            Objects.requireNonNull(encryptionEnabled, "EncryptionEnabled must not be null.");
            Objects.requireNonNull(encryptionPassphrase, "PassPhrase must not be null.");
            Objects.requireNonNull(acmCryptoUtils, "AcmCryptoUtils must not be null.");
            if (encryptionEnabled)
                return new String(acmCryptoUtils.decryptInputStreamWithPGP(new ByteArrayInputStream(dbData), encryptionPassphrase.toCharArray()));
            else return new String(dbData);
        } catch (AcmEncryptionBadKeyOrDataException e) {
            log.error("Error decrypting data.", e);
        }
        return null;
    }

    public static void setAcmDecryptionProperties(Boolean encryptionEnabled, AcmCryptoUtils acmCryptoUtils, String encryptionPassphrase, Boolean databaseEncryptionSupported) {
        StringEncryptionConverter.acmCryptoUtils = acmCryptoUtils;
        StringEncryptionConverter.encryptionPassphrase = encryptionPassphrase;
        StringEncryptionConverter.encryptionEnabled = encryptionEnabled;
        StringEncryptionConverter.databaseEncryptionSupported = databaseEncryptionSupported;
    }

    public static AcmCryptoUtils getAcmCryptoUtils() {
        return acmCryptoUtils;
    }

    public static Boolean getEncryptionEnabled() {
        return encryptionEnabled;
    }

    public static String getEncryptionPassphrase() {
        return encryptionPassphrase;
    }

    public static Boolean getDatabaseEncryptionSupported() {
        return databaseEncryptionSupported;
    }
}
