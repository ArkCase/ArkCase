package com.armedia.acm.services.email.service;

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
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

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

    @Before
    public void setUp() throws Exception
    {
        acmEmailContentGeneratorService.setAuthenticationTokenDao(mockAuthenticationTokenDao);
        acmEmailContentGeneratorService.setAuthenticationTokenService(mockAuthenticationTokenService);
    }

    @Test
    public void generateEmailBody_test() throws Exception
    {
        // given
        final String email = "user_email";
        final String header = "header";
        final String baseUrl = "base_url";
        final String title = "title";
        final String footer = "footer";
        final long fileId = 1234;
        final String token = "token";
        final String note = "<br/>" + baseUrl + fileId + "&acm_email_ticket=" + token + "<br/>";

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

        when(mockAuthenticationTokenService.getUncachedTokenForAuthentication(mockAuthentication)).thenReturn(token);
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setKey(token);
        authenticationToken.setStatus(AuthenticationTokenConstants.ACTIVE);
        authenticationToken.setEmail(email);
        authenticationToken.setFileId(fileId);

        when(mockAuthenticationTokenDao.save(any(AuthenticationToken.class))).thenReturn(authenticationToken);


        //when

        String emailContent = acmEmailContentGeneratorService.generateEmailBody(inputDTO, email, mockAuthentication);

        //then
        assertNotNull(emailContent);
        assertTrue(emailContent.contains(note));
        assertTrue(emailContent.contains(header));
        assertTrue(emailContent.contains(footer));
    }

}
