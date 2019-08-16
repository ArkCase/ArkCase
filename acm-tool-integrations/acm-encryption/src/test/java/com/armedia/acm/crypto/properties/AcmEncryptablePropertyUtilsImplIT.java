package com.armedia.acm.crypto.properties;

/*-
 * #%L
 * Acm Encryption Tools
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
        String password = "@rM3diA$";

        String encryptedPassword = utils.encryptPropertyValue(password);

        System.out.println("Encrypted password: " + encryptedPassword);
    }
}
