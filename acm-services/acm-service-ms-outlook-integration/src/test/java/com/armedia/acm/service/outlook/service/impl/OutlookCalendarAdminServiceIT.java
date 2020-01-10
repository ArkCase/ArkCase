/**
 *
 */
package com.armedia.acm.service.outlook.service.impl;

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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.calendar.config.service.EmailCredentials;
import com.armedia.acm.calendar.config.service.EmailCredentialsVerifierService;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.AcmCryptoUtils;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyEncryptionProperties;
import com.armedia.acm.service.outlook.dao.AcmOutlookFolderCreatorDao;
import com.armedia.acm.service.outlook.model.AcmOutlookFolderCreator;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 30, 2017
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-acm-encryption.xml", "/spring/spring-properties-encryption.xml" })
public class OutlookCalendarAdminServiceIT
{

    private static final String USER_ID = "OUTLOOK_CALENDAR_ADMIN_SERVICE";

    private static final String SYSTEM_PASSWORD = "password";

    private static final String SYSTEM_EMAIL = "email@email.com";

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    @Mock
    private AcmOutlookFolderCreatorDao mockedOutlookFolderCreatorDao;

    @Mock
    private EmailCredentialsVerifierService mockedVerifierService;

    @Autowired
    private AcmCryptoUtils cryptoUtils;

    @Autowired
    private AcmEncryptablePropertyEncryptionProperties encryptionProperties;

    @InjectMocks
    private OutlookCalendarAdminService outlookCalendarAdminService;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        outlookCalendarAdminService.setCryptoUtils(cryptoUtils);
        outlookCalendarAdminService.setEncryptionProperties(encryptionProperties);
    }

    @Test
    public void testGetFolderCreatorsWithInvalidCredentials() throws Exception
    {
        // given
        AcmOutlookFolderCreator creator1 = new AcmOutlookFolderCreator("a." + SYSTEM_EMAIL, encryptValue(SYSTEM_PASSWORD));
        creator1.setId(1L);
        AcmOutlookFolderCreator creator2 = new AcmOutlookFolderCreator("b." + SYSTEM_EMAIL, encryptValue(SYSTEM_PASSWORD));
        creator2.setId(2L);
        AcmOutlookFolderCreator creator3 = new AcmOutlookFolderCreator("c." + SYSTEM_EMAIL, encryptValue(SYSTEM_PASSWORD));
        creator3.setId(3L);
        when(mockedOutlookFolderCreatorDao.getFolderCreators()).thenReturn(Arrays.asList(creator1, creator2, creator3));
        when(mockedVerifierService.verifyEmailCredentials(USER_ID, new EmailCredentials("a." + SYSTEM_EMAIL, SYSTEM_PASSWORD)))
                .thenReturn(false);
        when(mockedVerifierService.verifyEmailCredentials(USER_ID, new EmailCredentials("b." + SYSTEM_EMAIL, SYSTEM_PASSWORD)))
                .thenReturn(true);
        when(mockedVerifierService.verifyEmailCredentials(USER_ID, new EmailCredentials("c." + SYSTEM_EMAIL, SYSTEM_PASSWORD)))
                .thenReturn(false);

        // when
        List<AcmOutlookFolderCreator> folderCreators = outlookCalendarAdminService.findFolderCreatorsWithInvalidCredentials();

        // then
        verify(mockedOutlookFolderCreatorDao).getFolderCreators();
        verify(mockedVerifierService, times(3)).verifyEmailCredentials(any(String.class), any(EmailCredentials.class));

        assertThat(folderCreators, containsInAnyOrder(creator1, creator3));

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

}
