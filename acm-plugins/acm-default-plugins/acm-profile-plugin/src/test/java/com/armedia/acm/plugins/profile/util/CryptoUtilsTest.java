package com.armedia.acm.plugins.profile.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class CryptoUtilsTest {

    String passwordToBeEncrypted;
    String userPassword;
    String md5Hex;

    @Before
    public void setUp() {
        passwordToBeEncrypted = "password";
        userPassword = "userPassword";
        //we must use only 16 bytes or we need to install "Cryptography Extension (JCE) Unlimited Strength."
        md5Hex = DigestUtils.md5Hex(userPassword).substring(0, 16);
    }

    @Test
    public void testEncryptData() throws Exception {
        byte[] encrypted = CryptoUtils.encryptData(md5Hex.getBytes(), passwordToBeEncrypted.getBytes());

        assertNotNull(encrypted);
    }

    @Test
    public void testDecryptData() throws Exception {
        byte[] encrypted = CryptoUtils.encryptData(md5Hex.getBytes(), passwordToBeEncrypted.getBytes());

        assertNotNull(encrypted);
        assertNotEquals(passwordToBeEncrypted, new String(encrypted));

        byte[] decryptData = CryptoUtils.decryptData(md5Hex.getBytes(), encrypted);

        assertEquals(passwordToBeEncrypted, new String(decryptData));
    }
}