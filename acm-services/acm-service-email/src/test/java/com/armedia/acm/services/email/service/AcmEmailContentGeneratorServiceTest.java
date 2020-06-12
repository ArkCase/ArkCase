package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
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

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.armedia.acm.core.ObjectLabelConfig;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationTokenConstants;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manoj.dhungana on 7/17/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AcmEmailContentGeneratorServiceTest
{
    @InjectMocks
    private AcmEmailContentGeneratorService acmEmailContentGeneratorService;

    @Mock
    private AuthenticationTokenService mockAuthenticationTokenService;

    @Mock
    private AuthenticationTokenDao mockAuthenticationTokenDao;

    @Mock
    private Authentication mockAuthentication;

    @Mock
    private EcmFileDao mockEcmFileDao;

    @Mock
    private ObjectLabelConfig objectLabelConfig;

    @Before
    public void setUp() throws Exception
    {
        acmEmailContentGeneratorService.setAuthenticationTokenDao(mockAuthenticationTokenDao);
        acmEmailContentGeneratorService.setAuthenticationTokenService(mockAuthenticationTokenService);
        acmEmailContentGeneratorService.setEcmFileDao(mockEcmFileDao);
        acmEmailContentGeneratorService.setObjectLabelConfig(objectLabelConfig);
    }

    @Test
    public void generateEmailBody_test()
    {
        // given
        final String email = "user_email";
        final String header = "header";
        final String baseUrl = "base_url";
        final String title = "title";
        final String footer = "footer";
        final long fileId = 1234;
        final String token = "token";
        final String version = "1.0";
        final String note = "<br/>" + fileId + "&version=" + version + "&acm_email_ticket=" + token + "<br/>";

        List<String> addresses = new ArrayList<>();
        addresses.add(email);
        List<Long> fileIds = new ArrayList<>();
        fileIds.add(fileId);

        EmailWithEmbeddedLinksDTO inputDTO = new EmailWithEmbeddedLinksDTO();
        inputDTO.setTitle(title);
        inputDTO.setHeader(header);
        inputDTO.setEmailAddresses(addresses);
        inputDTO.setBaseUrl(baseUrl);
        inputDTO.setFileIds(fileIds);
        inputDTO.setFooter(footer);

        EcmFile ecmFile = new EcmFile();
        ecmFile.setActiveVersionTag(version);

        when(mockAuthenticationTokenService.generateAndSaveAuthenticationToken(fileId, email, mockAuthentication))
                .thenReturn(token);
        when(mockEcmFileDao.find(Mockito.anyLong())).thenReturn(ecmFile);
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setKey(token);
        authenticationToken.setStatus(AuthenticationTokenConstants.ACTIVE);
        authenticationToken.setEmail(email);
        authenticationToken.setFileId(fileId);

        // when

        String emailContent = acmEmailContentGeneratorService.generateEmailBody(inputDTO, email, mockAuthentication);

        // then
        assertNotNull(emailContent);
        assertTrue(emailContent.contains(note));
        assertTrue(emailContent.contains(header));
        assertTrue(emailContent.contains(footer));
    }

}
