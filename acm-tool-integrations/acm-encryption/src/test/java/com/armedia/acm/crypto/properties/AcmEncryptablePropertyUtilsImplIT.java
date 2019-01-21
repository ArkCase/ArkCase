package com.armedia.acm.crypto.properties;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.AcmCryptoUtilsImpl;

import org.junit.Before;
import org.junit.Test;

public class AcmEncryptablePropertyUtilsImplIT
{
    private AcmEncryptablePropertyUtilsImpl utils;
    private AcmEncryptablePropertyEncryptionProperties encryptionProperties;

    @Before
    public void setup()
    {
        utils = new AcmEncryptablePropertyUtilsImpl();

        encryptionProperties = new AcmEncryptablePropertyEncryptionProperties();
        encryptionProperties.setCryptoUtils(new AcmCryptoUtilsImpl());
        encryptionProperties.setEncryptablePropertyUtils(utils);
        encryptionProperties.setPropertiesEncryptionAlgorithm("AES");
        encryptionProperties.setPropertiesEncryptionBlockCipherMode("CBC");
        encryptionProperties.setPropertiesEncryptionPadding("PKCS5Padding");
        encryptionProperties.setPropertiesEncryptionKeySize(256);
        encryptionProperties.setPropertiesEncryptionIVSize(128);
        encryptionProperties.setPropertiesEncryptionMagicSize(8);
        encryptionProperties.setPropertiesEncryptionSaltSize(8);
        encryptionProperties.setPropertiesEncryptionPassPhraseIterations(1);
        encryptionProperties.setPropertiesEncryptionPassPhraseHashAlgorithm("SHA256");
        encryptionProperties.setEncryptedSymmetricKeyEncryptionAlgorithm("RSA/ECB/PKCS1Padding");
        encryptionProperties
                .setEncryptedSymmetricKeyFilePath(System.getProperty("user.home") + "/.arkcase/acm/encryption/symmetricKey.encrypted");
        encryptionProperties.setKeystoreType("JKS");
        encryptionProperties.setKeystorePath(System.getProperty("user.home") + "/.arkcase/acm/private/keystore.old");
        encryptionProperties.setKeystorePassword("password");
        encryptionProperties.setPrivateKeyAlias("armedia");

        utils.setEncryptionProperties(encryptionProperties);
    }

    @Test
    public void decryptPassword() throws AcmEncryptionException
    {
        String encryptedPassword = "ENC(Ughl/4isisjxLIQyQ4vv6201Twu/CzZwpQmi94qgC4jkO7s8+HbmjX9kh/aWZb6n)";

        String decryptedPassword = utils.decryptPropertyValue(encryptedPassword);

        System.out.println("Decrypted password: " + decryptedPassword);
    }

    @Test
    public void encryptPassword() throws AcmEncryptionException
    {
        String password = "1qaz!QAZ2wsx@WSX";

        String encryptedPassword = utils.encryptPropertyValue(password);

        System.out.println("Encrypted password: " + encryptedPassword);
    }
}
