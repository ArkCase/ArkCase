package com.armedia.acm.crypto;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.core.exceptions.AcmEncryptionBadKeyOrDataException;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class AcmCryptoUtilsImplTest
{

    private String passwordToBeEncrypted;
    private String userPassword;
    private String sha256Hex;
    private AcmCryptoUtils cryptoUtils;

    @Before
    public void setUp() throws Exception
    {
        passwordToBeEncrypted = "password";
        userPassword = "userPassword";

        sha256Hex = DigestUtils.sha256Hex(userPassword);

        cryptoUtils = new AcmCryptoUtilsImpl();
    }

    @Test
    public void testEncryptData() throws Exception
    {
        byte[] encrypted = cryptoUtils.encryptData(sha256Hex.getBytes(), passwordToBeEncrypted.getBytes(), true);

        assertNotNull(encrypted);
    }

    @Test
    public void testDecryptData() throws Exception
    {
        byte[] encrypted = cryptoUtils.encryptData(sha256Hex.getBytes(), passwordToBeEncrypted.getBytes(), true);

        assertNotNull(encrypted);
        assertNotEquals(passwordToBeEncrypted, new String(encrypted));

        byte[] decryptData = cryptoUtils.decryptData(sha256Hex.getBytes(), encrypted, true);

        assertEquals(passwordToBeEncrypted, new String(decryptData));
    }

    @Test
    public void testPGPDecryptionValidPassPhrase() throws IOException, AcmEncryptionBadKeyOrDataException
    {
        Resource encryptedFile = new ClassPathResource("encrypted.bin");
        assertTrue(encryptedFile.exists());

        String passPhrase = "text";

        byte[] decrypted = cryptoUtils.decryptInputStreamWithPGP(encryptedFile.getInputStream(), passPhrase.toCharArray());

        assertArrayEquals("text".getBytes(), decrypted);
    }

    @Test(expected = AcmEncryptionBadKeyOrDataException.class)
    public void testPGPDecryptionInvalidPassPhrase() throws IOException, AcmEncryptionBadKeyOrDataException
    {
        Resource encryptedFile = new ClassPathResource("encrypted.bin");
        assertTrue(encryptedFile.exists());

        String passPhrase = "text1";

        cryptoUtils.decryptInputStreamWithPGP(encryptedFile.getInputStream(), passPhrase.toCharArray());

    }

}