package com.armedia.acm.crypto;

import com.armedia.acm.core.exceptions.AcmEncryptionBadKeyOrDataException;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-acm-encryption.xml"
})
public class AcmCryptoUtilsImplTest {

    private String passwordToBeEncrypted;
    private String userPassword;
    private String md5Hex;
    @Autowired
    private AcmCryptoUtils cryptoUtils;

    @Before
    public void setUp() {
        passwordToBeEncrypted = "password";
        userPassword = "userPassword";
        md5Hex = DigestUtils.md5Hex(userPassword);
    }

    @Test
    public void testEncryptData() throws Exception {
        byte[] encrypted = cryptoUtils.encryptData(md5Hex.getBytes(), passwordToBeEncrypted.getBytes(), true);

        assertNotNull(encrypted);
    }

    @Test
    public void testDecryptData() throws Exception {
        byte[] encrypted = cryptoUtils.encryptData(md5Hex.getBytes(), passwordToBeEncrypted.getBytes(), true);

        assertNotNull(encrypted);
        assertNotEquals(passwordToBeEncrypted, new String(encrypted));

        byte[] decryptData = cryptoUtils.decryptData(md5Hex.getBytes(), encrypted, true);

        assertEquals(passwordToBeEncrypted, new String(decryptData));
    }

    @Test
    public void testPGPDecryptionValidPassPhrase() throws IOException, AcmEncryptionBadKeyOrDataException {
        Resource encryptedFile = new ClassPathResource("encrypted.bin");
        assertTrue(encryptedFile.exists());

        String passPhrase = "text";

        byte[] decrypted = cryptoUtils.decryptInputStreamWithPGP(encryptedFile.getInputStream(), passPhrase.toCharArray());

        assertArrayEquals("text".getBytes(), decrypted);
    }

    @Test(expected = AcmEncryptionBadKeyOrDataException.class)
    public void testPGPDecryptionInvalidPassPhrase() throws IOException, AcmEncryptionBadKeyOrDataException {
        Resource encryptedFile = new ClassPathResource("encrypted.bin");
        assertTrue(encryptedFile.exists());

        String passPhrase = "text1";

        cryptoUtils.decryptInputStreamWithPGP(encryptedFile.getInputStream(), passPhrase.toCharArray());

    }

}