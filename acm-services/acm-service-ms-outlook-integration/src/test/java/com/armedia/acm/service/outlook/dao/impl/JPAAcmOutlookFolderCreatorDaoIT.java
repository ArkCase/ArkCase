package com.armedia.acm.service.outlook.dao.impl;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
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
import java.util.Arrays;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 8, 2017
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-acm-encryption.xml", "/spring/spring-properties-encryption.xml" })
public class JPAAcmOutlookFolderCreatorDaoIT
{

    private static final String SYSTEM_EMAIL_ADDRESS_FIELD = "systemEmailAddress";

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

    // @Mock
    // private EmailCredentialsVerifierService mockedVerifierService;

    @Mock
    private AcmOutlookFolderCreator mockedFolderCreator;

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
        }
        catch (AcmOutlookFolderCreatorDaoException e)
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
        verify(mockedFolderCreatorQuery).setParameter(SYSTEM_EMAIL_ADDRESS_FIELD, SYSTEM_EMAIL);
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
        verify(mockedFolderCreatorQuery).setParameter(SYSTEM_EMAIL_ADDRESS_FIELD, SYSTEM_EMAIL);
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
        }
        catch (AcmOutlookFolderCreatorDaoException e)
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

    @Test
    public void testGetFolderCreators() throws Exception
    {
        // given
        when(mockedEm.createQuery(any(String.class), eq(AcmOutlookFolderCreator.class))).thenReturn(mockedFolderCreatorQuery);
        AcmOutlookFolderCreator creator1 = new AcmOutlookFolderCreator("a." + SYSTEM_EMAIL, encryptValue(SYSTEM_PASSWORD));
        creator1.setId(1L);
        AcmOutlookFolderCreator creator2 = new AcmOutlookFolderCreator("b." + SYSTEM_EMAIL, encryptValue(SYSTEM_PASSWORD));
        creator2.setId(2L);
        AcmOutlookFolderCreator creator3 = new AcmOutlookFolderCreator("c." + SYSTEM_EMAIL, encryptValue(SYSTEM_PASSWORD));
        creator3.setId(3L);
        when(mockedFolderCreatorQuery.getResultList()).thenReturn(Arrays.asList(creator1, creator2, creator3));

        // when
        List<AcmOutlookFolderCreator> folderCreators = outlookFolderCreatorDao.getFolderCreators();

        // then
        verify(mockedEm).createQuery("SELECT ofc FROM AcmOutlookFolderCreator ofc", AcmOutlookFolderCreator.class);
        verify(mockedFolderCreatorQuery).getResultList();

        assertThat(folderCreators, containsInAnyOrder(creator1, creator2, creator3));

    }

    @Test
    public void testUpdateFolderCreator() throws Exception
    {
        // given
        AcmOutlookFolderCreator updatedCreator = new AcmOutlookFolderCreator(SYSTEM_EMAIL, SYSTEM_PASSWORD);
        updatedCreator.setId(1L);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // when
        outlookFolderCreatorDao.updateFolderCreator(mockedFolderCreator, updatedCreator);

        // then
        verify(mockedFolderCreator).setSystemEmailAddress(SYSTEM_EMAIL);
        verify(mockedFolderCreator).setSystemPassword(captor.capture());
        assertThat(decryptValue(captor.getValue()), is(SYSTEM_PASSWORD));
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
