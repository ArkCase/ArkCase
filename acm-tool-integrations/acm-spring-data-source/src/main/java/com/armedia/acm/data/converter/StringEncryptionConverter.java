package com.armedia.acm.data.converter;

import com.armedia.acm.core.exceptions.AcmEncryptionBadKeyOrDataException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.io.ByteArrayInputStream;
import java.util.Objects;

/**
 * Created by nebojsha on 05.08.2015.
 */
public class StringEncryptionConverter implements AttributeConverter<String, byte[]> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static AcmCryptoUtils acmCryptoUtils;

    private static Boolean encryptionEnabled;
    private static String encryptionPassphrase;

    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
        return attribute.getBytes();
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData) {
        try {
            Objects.requireNonNull(encryptionPassphrase, "PassPhrase must be not null.");
            Objects.requireNonNull(acmCryptoUtils, "AcmCryptoUtils must be not null.");
            return new String(acmCryptoUtils.decryptInputStreamWithPGP(new ByteArrayInputStream(dbData), encryptionPassphrase.toCharArray()));
        } catch (AcmEncryptionBadKeyOrDataException e) {
            log.error("Error decrypting data.", e);
        }
        return null;
    }

    public static void setAcmDecryptionProperties(Boolean encryptionEnabled, AcmCryptoUtils acmCryptoUtils, String encryptionPassphrase) {
        StringEncryptionConverter.acmCryptoUtils = acmCryptoUtils;
        StringEncryptionConverter.encryptionPassphrase = encryptionPassphrase;
        StringEncryptionConverter.encryptionEnabled = encryptionEnabled;
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
}
