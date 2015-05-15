package com.armedia.acm.crypto;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-acm-encryption.xml"
})
public class AcmCryptoUtilsImplTest {

    String passwordToBeEncrypted;
    String userPassword;
    String md5Hex;
    @Autowired
    AcmCryptoUtils cryptoUtils;

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
}