package com.armedia.acm.services.dataupdate.service;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.AcmCryptoUtilsImpl;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyEncryptionProperties;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class OutlookFolderCreatorPasswordMd5ToSha256UpdateExecutorTest extends EasyMockSupport
{

    private OutlookFolderCreatorPasswordMd5ToSha256UpdateExecutor unit;
    private EntityManager mockEntityManager;
    private TypedQuery mockQuery;
    private AcmCryptoUtils cryptoUtils;
    private AcmEncryptablePropertyEncryptionProperties encryptionProperties;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp()
    {
        mockEntityManager = createMock(EntityManager.class);
        cryptoUtils = new AcmCryptoUtilsImpl();
        mockQuery = createMock(TypedQuery.class);

        unit = new OutlookFolderCreatorPasswordMd5ToSha256UpdateExecutor();
        unit.setEntityManager(mockEntityManager);
        unit.setCryptoUtils(cryptoUtils);

        encryptionProperties = new AcmEncryptablePropertyEncryptionProperties();
        encryptionProperties.setPropertiesEncryptionAlgorithm("AES");
        encryptionProperties.setPropertiesEncryptionBlockCipherMode("CBC");
        encryptionProperties.setPropertiesEncryptionIVSize(128);
        encryptionProperties.setPropertiesEncryptionKeySize(256);
        encryptionProperties.setPropertiesEncryptionMagicSize(8);
        encryptionProperties.setPropertiesEncryptionPadding("PKCS5Padding");
        encryptionProperties.setPropertiesEncryptionPassPhraseHashAlgorithm("SHA256");
        encryptionProperties.setPropertiesEncryptionPassPhraseIterations(1);
        encryptionProperties.setPropertiesEncryptionSaltSize(8);
        encryptionProperties.setSymmetricKey("test-key".getBytes());

        unit.setCryptoProperties(encryptionProperties);
    }

    @Test
    public void updateOutlookFolderCreatorPasswords() throws Exception
    {
        AcmOutlookFolderCreator first = buildOutlookFolderCreator(500L, "jgarcia@dead.net", "MD5");
        String firstMd5Password = first.getSystemPassword();

        AcmOutlookFolderCreator second = buildOutlookFolderCreator(600L, "bweir@dead.net", "SHA256");

        List<AcmOutlookFolderCreator> found = Arrays.asList(first, second);

        expect(mockEntityManager.createQuery("SELECT e FROM AcmOutlookFolderCreator e", AcmOutlookFolderCreator.class))
                .andReturn(mockQuery);
        expect(mockQuery.getResultList()).andReturn(found);

        expect(mockEntityManager.merge(first)).andReturn(first).atLeastOnce();

        replayAll();

        unit.updateOutlookFolderCreatorPasswords();

        verifyAll();

        // first should have an updated SHA256 password
        String firstSha256Password = first.getSystemPassword();
        assertNotEquals(firstMd5Password, firstSha256Password);

        byte[] decryptedNewPassword = cryptoUtils.decryptData(
                encryptionProperties.getSymmetricKey(),
                Base64.getDecoder().decode(firstSha256Password),
                encryptionProperties.getPropertiesEncryptionKeySize(),
                encryptionProperties.getPropertiesEncryptionIVSize(),
                encryptionProperties.getPropertiesEncryptionMagicSize(),
                encryptionProperties.getPropertiesEncryptionSaltSize(),
                encryptionProperties.getPropertiesEncryptionPassPhraseIterations(),
                encryptionProperties.getPropertiesEncryptionPassPhraseHashAlgorithm(),
                encryptionProperties.getPropertiesEncryptionAlgorithm(),
                encryptionProperties.getPropertiesEncryptionBlockCipherMode(),
                encryptionProperties.getPropertiesEncryptionPadding());
        String decrypted = new String(decryptedNewPassword);
        assertEquals("test-password", decrypted);

    }

    private AcmOutlookFolderCreator buildOutlookFolderCreator(long id, String email, String hashAlgorithm) throws Exception
    {
        byte[] encrypted = cryptoUtils.encryptData(
                encryptionProperties.getSymmetricKey(),
                "test-password".getBytes(),
                encryptionProperties.getPropertiesEncryptionKeySize(),
                encryptionProperties.getPropertiesEncryptionIVSize(),
                encryptionProperties.getPropertiesEncryptionMagicSize(),
                encryptionProperties.getPropertiesEncryptionSaltSize(),
                encryptionProperties.getPropertiesEncryptionPassPhraseIterations(),
                hashAlgorithm,
                encryptionProperties.getPropertiesEncryptionAlgorithm(),
                encryptionProperties.getPropertiesEncryptionBlockCipherMode(),
                encryptionProperties.getPropertiesEncryptionPadding());
        String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);

        log.debug("Base64 encrypted password: {}", encryptedBase64);

        AcmOutlookFolderCreator creator = new AcmOutlookFolderCreator();
        creator.setId(id);
        creator.setSystemEmailAddress(email);
        creator.setSystemPassword(encryptedBase64);
        return creator;
    }
}
