package com.armedia.acm.crypto;

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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.crypto.exceptions.AcmEncryptionBadKeyOrDataException;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Ignore
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
