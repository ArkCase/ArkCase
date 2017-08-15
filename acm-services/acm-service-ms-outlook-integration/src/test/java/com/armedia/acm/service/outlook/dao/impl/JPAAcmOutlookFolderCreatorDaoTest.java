package com.armedia.acm.service.outlook.dao.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyEncryptionProperties;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDaoException;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;
import com.armedia.acm.service.outlook.model.AcmOutlookObjectReference;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 8, 2017
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-acm-encryption.xml", "/spring/spring-properties-encryption.xml" })
public class JPAAcmOutlookFolderCreatorDaoTest
{

    private static final String SYSTEM_PASSWORD = "password";

    private static final String SYSTEM_EMAIL = "email@email.com";

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    private static final long REFERENCE_ID = 101L;

    private static final String CASE_FILE = "CASE_FILE";

    @Mock
    private EntityManager mockedEm;

    @Mock
    private TypedQuery<AcmOutlookObjectReference> mockedObjectReferenceQuery;

    @Mock
    private TypedQuery<AcmOutlookFolderCreator> mockedFolderCreatorQuery;

    @Mock
    private List<AcmOutlookFolderCreator> mockedResultList;

    @Autowired
    private AcmCryptoUtils cryptoUtils;

    @Autowired
    private AcmEncryptablePropertyEncryptionProperties encryptionProperties;

    @InjectMocks
    private JPAAcmOutlookFolderCreatorDao outlookFolderCreatorDao;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        outlookFolderCreatorDao.setCryptoUtils(cryptoUtils);
        outlookFolderCreatorDao.setEncryptionProperties(encryptionProperties);
    }

    @Test
    public void testGetOutlookObjectReference() throws Exception
    {
        // given
        when(mockedEm.createQuery(any(String.class), eq(AcmOutlookObjectReference.class))).thenReturn(mockedObjectReferenceQuery);
        AcmOutlookObjectReference objectReferenceStub = new AcmOutlookObjectReference();
        objectReferenceStub.setId(REFERENCE_ID);
        objectReferenceStub.setObjectType(CASE_FILE);
        when(mockedObjectReferenceQuery.getSingleResult()).thenReturn(objectReferenceStub);

        // when
        AcmOutlookObjectReference objectReference = outlookFolderCreatorDao.getOutlookObjectReference(REFERENCE_ID, CASE_FILE);

        // then
        assertThat(objectReference.getId(), is(REFERENCE_ID));
        assertThat(objectReference.getObjectType(), is(CASE_FILE));

        verify(mockedEm).createQuery(
                "SELECT oor FROM AcmOutlookObjectReference oor WHERE oor.objectId = :objectId AND oor.objectType = :objectType",
                AcmOutlookObjectReference.class);
        verify(mockedObjectReferenceQuery).setParameter("objectId", REFERENCE_ID);
        verify(mockedObjectReferenceQuery).setParameter("objectType", CASE_FILE);
        verify(mockedObjectReferenceQuery).getSingleResult();

    }

    @Test
    public void testGetOutlookObjectReference_throwsException() throws Exception
    {
        // given
        when(mockedEm.createQuery(any(String.class), eq(AcmOutlookObjectReference.class))).thenReturn(mockedObjectReferenceQuery);
        AcmOutlookObjectReference objectReferenceStub = new AcmOutlookObjectReference();
        objectReferenceStub.setId(REFERENCE_ID);
        objectReferenceStub.setObjectType(CASE_FILE);
        when(mockedObjectReferenceQuery.getSingleResult()).thenThrow(new PersistenceException());

        // when
        Exception expectedEx = null;
        try
        {
            outlookFolderCreatorDao.getOutlookObjectReference(REFERENCE_ID, CASE_FILE);
        } catch (AcmOutlookFolderCreatorDaoException e)
        {
            expectedEx = e;
        }

        // then
        assertThat(expectedEx.getMessage(),
                is(String.format("Error while retrieving 'AcmOutlookObjectReference' instance for objectId [%s] and objectType [%s].",
                        REFERENCE_ID, CASE_FILE)));
        assertThat(PersistenceException.class.isAssignableFrom(expectedEx.getCause().getClass()), is(true));

        verify(mockedEm).createQuery(
                "SELECT oor FROM AcmOutlookObjectReference oor WHERE oor.objectId = :objectId AND oor.objectType = :objectType",
                AcmOutlookObjectReference.class);
        verify(mockedObjectReferenceQuery).setParameter("objectId", REFERENCE_ID);
        verify(mockedObjectReferenceQuery).setParameter("objectType", CASE_FILE);
        verify(mockedObjectReferenceQuery).getSingleResult();

    }

    @Test
    public void testGetFolderCreator_existingUser() throws Exception
    {
        // given
        when(mockedEm.createQuery(any(String.class), eq(AcmOutlookFolderCreator.class))).thenReturn(mockedFolderCreatorQuery);
        when(mockedFolderCreatorQuery.getResultList()).thenReturn(mockedResultList);
        when(mockedResultList.isEmpty()).thenReturn(false);
        AcmOutlookFolderCreator folderCreatorStub = new AcmOutlookFolderCreator(SYSTEM_EMAIL, encryptValue(SYSTEM_PASSWORD));
        when(mockedResultList.get(0)).thenReturn(folderCreatorStub);

        // when
        AcmOutlookFolderCreator folderCreator = outlookFolderCreatorDao.getFolderCreator(SYSTEM_EMAIL, SYSTEM_PASSWORD);

        // then
        assertThat(folderCreator.getSystemEmailAddress(), is(SYSTEM_EMAIL));
        assertThat(folderCreator.getSystemPassword(), is(SYSTEM_PASSWORD));

        verify(mockedEm).createQuery("SELECT ofc FROM AcmOutlookFolderCreator ofc WHERE ofc.systemEmailAddress = :systemEmailAddress",
                AcmOutlookFolderCreator.class);
        verify(mockedFolderCreatorQuery).setParameter("systemEmailAddress", SYSTEM_EMAIL);
        verify(mockedFolderCreatorQuery).getResultList();
        verify(mockedResultList).isEmpty();
        verify(mockedResultList).get(0);

    }

    @Test
    public void testGetFolderCreator_nonExistingUser() throws Exception
    {
        // given
        when(mockedEm.createQuery(any(String.class), eq(AcmOutlookFolderCreator.class))).thenReturn(mockedFolderCreatorQuery);
        when(mockedFolderCreatorQuery.getResultList()).thenReturn(mockedResultList);
        when(mockedResultList.isEmpty()).thenReturn(true);
        AcmOutlookFolderCreator folderCreatorStub = new AcmOutlookFolderCreator(SYSTEM_EMAIL, encryptValue(SYSTEM_PASSWORD));
        when(mockedEm.merge(any(AcmOutlookFolderCreator.class))).thenReturn(folderCreatorStub);

        // when
        AcmOutlookFolderCreator folderCreator = outlookFolderCreatorDao.getFolderCreator(SYSTEM_EMAIL, SYSTEM_PASSWORD);

        // then
        assertThat(folderCreator.getSystemEmailAddress(), is(SYSTEM_EMAIL));
        assertThat(folderCreator.getSystemPassword(), is(SYSTEM_PASSWORD));

        verify(mockedEm).createQuery("SELECT ofc FROM AcmOutlookFolderCreator ofc WHERE ofc.systemEmailAddress = :systemEmailAddress",
                AcmOutlookFolderCreator.class);
        verify(mockedFolderCreatorQuery).setParameter("systemEmailAddress", SYSTEM_EMAIL);
        verify(mockedFolderCreatorQuery).getResultList();
        verify(mockedResultList).isEmpty();
        verify(mockedEm).merge(any(AcmOutlookFolderCreator.class));
        verify(mockedEm).detach(folderCreatorStub);

    }

    @Test
    public void testGetFolderCreatorForObject() throws Exception
    {
        // given
        when(mockedEm.createQuery(any(String.class), eq(AcmOutlookFolderCreator.class))).thenReturn(mockedFolderCreatorQuery);
        AcmOutlookFolderCreator folderCreatorStub = new AcmOutlookFolderCreator(SYSTEM_EMAIL, encryptValue(SYSTEM_PASSWORD));
        when(mockedFolderCreatorQuery.getSingleResult()).thenReturn(folderCreatorStub);

        // when
        AcmOutlookFolderCreator folderCreator = outlookFolderCreatorDao.getFolderCreatorForObject(REFERENCE_ID, CASE_FILE);

        // then
        assertThat(folderCreator.getSystemEmailAddress(), is(SYSTEM_EMAIL));
        assertThat(folderCreator.getSystemPassword(), is(SYSTEM_PASSWORD));

        verify(mockedEm).createQuery(
                "SELECT ofc FROM AcmOutlookFolderCreator ofc JOIN ofc.outlookObjectReferences oor WHERE oor.objectId = :objectId AND oor.objectType = :objectType",
                AcmOutlookFolderCreator.class);
        verify(mockedFolderCreatorQuery).setParameter("objectId", REFERENCE_ID);
        verify(mockedFolderCreatorQuery).setParameter("objectType", CASE_FILE);
        verify(mockedFolderCreatorQuery).getSingleResult();

    }

    @Test
    public void testGetFolderCreatorForObject_throwsException() throws Exception
    {
        // given
        when(mockedEm.createQuery(any(String.class), eq(AcmOutlookFolderCreator.class))).thenReturn(mockedFolderCreatorQuery);
        new AcmOutlookFolderCreator(SYSTEM_EMAIL, encryptValue(SYSTEM_PASSWORD));
        when(mockedFolderCreatorQuery.getSingleResult()).thenThrow(new PersistenceException());

        // when
        Exception expectedEx = null;
        try
        {
            outlookFolderCreatorDao.getFolderCreatorForObject(REFERENCE_ID, CASE_FILE);
        } catch (AcmOutlookFolderCreatorDaoException e)
        {
            expectedEx = e;
        }

        // then
        assertThat(expectedEx.getMessage(),
                is(String.format(
                        "Error while retrieving 'AcmOutlookFolderCreator' instance associated with the AcmOutlookObjectReference instance with objectId [%s] and objectType [%s].",
                        REFERENCE_ID, CASE_FILE)));
        assertThat(PersistenceException.class.isAssignableFrom(expectedEx.getCause().getClass()), is(true));

        verify(mockedEm).createQuery(
                "SELECT ofc FROM AcmOutlookFolderCreator ofc JOIN ofc.outlookObjectReferences oor WHERE oor.objectId = :objectId AND oor.objectType = :objectType",
                AcmOutlookFolderCreator.class);
        verify(mockedFolderCreatorQuery).setParameter("objectId", REFERENCE_ID);
        verify(mockedFolderCreatorQuery).setParameter("objectType", CASE_FILE);
        verify(mockedFolderCreatorQuery).getSingleResult();

    }

    @Test
    public void testRecordFolderCreator() throws Exception
    {
        // given
        AcmOutlookFolderCreator creator = new AcmOutlookFolderCreator(SYSTEM_EMAIL, SYSTEM_PASSWORD);
        ArgumentCaptor<AcmOutlookObjectReference> captor = ArgumentCaptor.forClass(AcmOutlookObjectReference.class);

        // when
        outlookFolderCreatorDao.recordFolderCreator(creator, REFERENCE_ID, CASE_FILE);

        // then
        verify(mockedEm).merge(captor.capture());

        AcmOutlookObjectReference captured = captor.getValue();
        assertThat(captured.getObjectId(), is(REFERENCE_ID));
        assertThat(captured.getObjectType(), is(CASE_FILE));
        assertThat(captured.getFolderCreator().getSystemEmailAddress(), is(SYSTEM_EMAIL));
        assertThat(decryptValue(captured.getFolderCreator().getSystemPassword()), is(SYSTEM_PASSWORD));

    }

    private String encryptValue(String plainText) throws AcmEncryptionException
    {
        String encryptedValue = Base64.encodeBase64String(encryptionProperties.getCryptoUtils().encryptData(
                encryptionProperties.getSymmetricKey(), plainText.getBytes(UTF8_CHARSET),
                encryptionProperties.getPropertiesEncryptionKeySize(), encryptionProperties.getPropertiesEncryptionIVSize(),
                encryptionProperties.getPropertiesEncryptionMagicSize(), encryptionProperties.getPropertiesEncryptionSaltSize(),
                encryptionProperties.getPropertiesEncryptionPassPhraseIterations(),
                encryptionProperties.getPropertiesEncryptionPassPhraseHashAlgorithm(),
                encryptionProperties.getPropertiesEncryptionAlgorithm(), encryptionProperties.getPropertiesEncryptionBlockCipherMode(),
                encryptionProperties.getPropertiesEncryptionPadding()));
        return encryptedValue;
    }

    private String decryptValue(String encrypted) throws AcmEncryptionException
    {
        String decryptedValue = new String(encryptionProperties.getCryptoUtils().decryptData(encryptionProperties.getSymmetricKey(),
                Base64.decodeBase64(encrypted), encryptionProperties.getPropertiesEncryptionKeySize(),
                encryptionProperties.getPropertiesEncryptionIVSize(), encryptionProperties.getPropertiesEncryptionMagicSize(),
                encryptionProperties.getPropertiesEncryptionSaltSize(), encryptionProperties.getPropertiesEncryptionPassPhraseIterations(),
                encryptionProperties.getPropertiesEncryptionPassPhraseHashAlgorithm(),
                encryptionProperties.getPropertiesEncryptionAlgorithm(), encryptionProperties.getPropertiesEncryptionBlockCipherMode(),
                encryptionProperties.getPropertiesEncryptionPadding()), UTF8_CHARSET);
        return decryptedValue;
    }

}
