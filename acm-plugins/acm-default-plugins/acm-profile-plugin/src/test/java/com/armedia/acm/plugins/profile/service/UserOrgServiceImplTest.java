package com.armedia.acm.plugins.profile.service;

/*-
 * #%L
 * ACM Default Plugin: Profile
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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.service.OrganizationService;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.model.ProfileDTO;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UserOrgServiceImplTest extends EasyMockSupport
{
    private static final String USER_ID = "ann-acm";
    private static final String COMPANY_NAME = "ARMEDIA";
    private static final String EXCEPTION_PROPERTY = "saveException";
    private static final String DEFAULT_CMIS_ID = "alfresco";
    private UserDao mockUserDao;
    private UserOrgDao mockUserOrgDao;
    private OrganizationService mockOrganizationService;
    private UserOrgServiceImpl userOrgService;
    private Authentication mockAuthentication;
    private ProfileEventPublisher mockEventPublisher;
    private CamelContextManager mockCamelContextManager;
    private Map<String, Object> messageProps;
    private AuditPropertyEntityAdapter mockAuditPropertyEntityAdapter;

    @Before
    public void setUp()
    {
        mockAuthentication = createMock(AcmAuthentication.class);
        mockUserDao = createMock(UserDao.class);
        mockUserOrgDao = createMock(UserOrgDao.class);
        mockOrganizationService = createMock(OrganizationService.class);
        mockEventPublisher = createMock(ProfileEventPublisher.class);
        mockCamelContextManager = createMock(CamelContextManager.class);
        mockAuditPropertyEntityAdapter = createMock(AuditPropertyEntityAdapter.class);

        userOrgService = new UserOrgServiceImpl();
        userOrgService.setUserDao(mockUserDao);
        userOrgService.setUserOrgDao(mockUserOrgDao);
        userOrgService.setOrganizationService(mockOrganizationService);
        userOrgService.setEventPublisher(mockEventPublisher);
        userOrgService.setCamelContextManager(mockCamelContextManager);
        userOrgService.setAuditPropertyEntityAdapter(mockAuditPropertyEntityAdapter);

        EcmFileConfig ecmFileConfig = new EcmFileConfig();
        ecmFileConfig.setDefaultCmisId(DEFAULT_CMIS_ID);
        userOrgService.setEcmFileConfig(ecmFileConfig);

        messageProps = new LinkedHashMap<>();
        messageProps.put("acmUser", mockAuthentication);
    }

    @Test
    public void saveUserOrgInfoWhenCompanyNameIsWhiteSpaceOnlyAndUserOrgExists() throws ArkCaseFileRepositoryException
    {
        ProfileDTO profileDTO = createMockProfileDTO();
        profileDTO.setCompanyName(" ");

        UserOrg userOrg = createAndSetUserOrg();

        expectWhenUpdateUserOrgSuccessfully(userOrg);

        // user organization exists, newUserOrg = false
        // userOrg is saved, succeeded = true
        mockEventPublisher.publishProfileEvent(userOrg, mockAuthentication, false, true);
        expectLastCall().once();

        replayAll();

        userOrgService.saveUserOrgInfo(profileDTO, mockAuthentication);

        verifyAll();

        verifyUserOrgIsPopulated(userOrg, profileDTO);
        // companyName is blank string, organization should be null
        assertNull(userOrg.getOrganization());
    }

    private void expectWhenUpdateUserOrgSuccessfully(UserOrg userOrg) throws ArkCaseFileRepositoryException
    {
        expect(mockAuthentication.getName()).andReturn(USER_ID);
        expect(userOrgService.getUserOrgForUserId(USER_ID)).andReturn(userOrg);

        expect(mockCamelContextManager.send(ArkCaseCMISActions.GET_OR_CREATE_FOLDER_BY_PATH, messageProps)).andReturn(Folder.class);
    }

    private UserOrg createAndSetUserOrg()
    {
        AcmUser user = new AcmUser();
        user.setUserId(USER_ID);

        Organization organization = new Organization();
        organization.setOrganizationValue(COMPANY_NAME);

        UserOrg userOrg = new UserOrg();
        userOrg.setUser(user);
        userOrg.setOrganization(organization);

        return userOrg;
    }

    @Test
    public void saveUserOrgInfoWhenCompanyNameNotBlankAndUserOrgExists() throws ArkCaseFileRepositoryException
    {
        ProfileDTO profileDTO = createMockProfileDTO();

        UserOrg userOrg = createAndSetUserOrg();

        expectWhenUpdateUserOrgSuccessfully(userOrg);
        expect(mockOrganizationService.findOrCreateOrganization(COMPANY_NAME, USER_ID))
                .andReturn(userOrg.getOrganization());

        // user organization exists, newUserOrg = false
        // userOrg is saved, succeeded = true
        mockEventPublisher.publishProfileEvent(userOrg, mockAuthentication, false, true);
        expectLastCall().once();

        replayAll();

        userOrgService.saveUserOrgInfo(profileDTO, mockAuthentication);

        verifyAll();

        verifyUserOrgIsPopulated(userOrg, profileDTO);
    }

    @Test
    public void saveUserOrgInfoWhenCompanyNameNotBlankAndUserOrgNull()
    {
        ProfileDTO profileDTO = createMockProfileDTO();

        UserOrg expectedUserOrg = createAndSetUserOrg();
        expectedUserOrg.setUserOrgId(1L);

        expect(mockAuthentication.getName()).andReturn(USER_ID);
        expect(userOrgService.getUserOrgForUserId(USER_ID)).andReturn(null);
        expect(mockUserDao.findByUserId(USER_ID)).andReturn(expectedUserOrg.getUser());
        expect(mockOrganizationService.findOrCreateOrganization(COMPANY_NAME, USER_ID))
                .andReturn(expectedUserOrg.getOrganization());

        Capture<UserOrg> captureUserOrg = Capture.newInstance();
        Capture<UserOrg> captureSavedUserOrg = Capture.newInstance();

        /*
         * expect(mockMuleContextManager.send(eq(MULE_ENDPOINT), capture(captureUserOrg),
         * eq(muleMessageProps))).andReturn(mockMuleMessage);
         */

        /*
         * expect(mockMuleMessage.getPayload(UserOrg.class)).andReturn(expectedUserOrg);
         * expect(mockMuleMessage.getInboundProperty(EXCEPTION_PROPERTY)).andReturn(mockMuleException);
         */

        // user organization should be created, newUserOrg = true
        // userOrg is saved, succeeded = true
        mockEventPublisher.publishProfileEvent(capture(captureSavedUserOrg),
                eq(mockAuthentication), eq(true), eq(true));
        expectLastCall().once();

        replayAll();

        userOrgService.saveUserOrgInfo(profileDTO, mockAuthentication);

        verifyAll();

        verifyUserOrgIsPopulated(captureUserOrg.getValue(), profileDTO);
        assertEquals(captureUserOrg.getValue().getOrganization(), expectedUserOrg.getOrganization());
        assertEquals(captureUserOrg.getValue().getUser(), expectedUserOrg.getUser());
        assertNotNull(captureSavedUserOrg.getValue().getUserOrgId());
    }

    @Test
    public void saveUserOrgInfoWhenCompanyNameNotBlankUserOrgNullAndFailedToBeCreated()
    {
        ProfileDTO profileDTO = createMockProfileDTO();

        UserOrg expectedUserOrg = createAndSetUserOrg();
        expectedUserOrg.setUserOrgId(1L);

        expect(mockAuthentication.getName()).andReturn(USER_ID);
        expect(userOrgService.getUserOrgForUserId(USER_ID)).andReturn(null);
        expect(mockUserDao.findByUserId(USER_ID)).andReturn(expectedUserOrg.getUser());
        expect(mockOrganizationService.findOrCreateOrganization(COMPANY_NAME, USER_ID))
                .andReturn(expectedUserOrg.getOrganization());

        Capture<UserOrg> captureUserOrg = Capture.newInstance();

        /*
         * expect(mockMuleContextManager.send(eq(MULE_ENDPOINT), capture(captureUserOrg),
         * eq(muleMessageProps))).andReturn(mockMuleMessage);
         */

        // user organization should be created, newUserOrg = true
        // userOrg is saved, succeeded = false
        mockEventPublisher.publishProfileEvent(capture(captureUserOrg), eq(mockAuthentication), eq(true), eq(false));
        expectLastCall().once();

        replayAll();

        userOrgService.saveUserOrgInfo(profileDTO, mockAuthentication);

        verifyAll();

        verifyUserOrgIsPopulated(captureUserOrg.getValue(), profileDTO);
        assertEquals(captureUserOrg.getValue().getOrganization(), expectedUserOrg.getOrganization());
        assertEquals(captureUserOrg.getValue().getUser(), expectedUserOrg.getUser());
        assertNull(captureUserOrg.getValue().getUserOrgId());
    }

    @Test
    public void getProfileInfoWhenUserOrgExists()
    {
        UserOrg expectedUserOrg = createAndSetUserOrg();
        expectedUserOrg.setWebsite("unknown.com");

        AcmUser user = new AcmUser();
        user.setUserId(USER_ID);

        AcmGroup group = new AcmGroup();
        group.setName("ACM_INVESTIGATOR");

        Set<AcmGroup> groups = new HashSet<>();
        groups.add(group);

        user.setGroups(groups);

        expect(mockUserOrgDao.findByUserId(USER_ID)).andReturn(expectedUserOrg);
        expect(mockUserDao.findByUserId(USER_ID)).andReturn(user);

        replayAll();

        ProfileDTO profileDTO = userOrgService.getProfileInfo(USER_ID, mockAuthentication);

        verifyAll();

        assertEquals(expectedUserOrg.getWebsite(), profileDTO.getWebsite());
        assertEquals(expectedUserOrg.getUser().getUserId(), profileDTO.getUserId());
        assertEquals(group.getName(), profileDTO.getGroups().get(0));
        assertEquals(expectedUserOrg.getOrganization().getOrganizationValue(), profileDTO.getCompanyName());
    }

    @Test
    public void getProfileInfoWhenUserOrgNull()
    {
        AcmUser user = new AcmUser();
        user.setUserId(USER_ID);
        UserOrg expectedUserOrg = new UserOrg();
        expectedUserOrg.setUser(user);

        AcmGroup group = new AcmGroup();
        group.setName("ACM_INVESTIGATOR");

        Set<AcmGroup> groups = new HashSet<>();
        groups.add(group);

        user.setGroups(groups);

        expect(mockUserOrgDao.findByUserId(USER_ID)).andReturn(null);
        expect(mockUserDao.findByUserId(USER_ID)).andReturn(user);
        expect(mockUserDao.findByUserId(USER_ID)).andReturn(user);


        Capture<UserOrg> captureUserOrg = Capture.newInstance();
        /*
         * expect(mockMuleContextManager.send(eq(MULE_ENDPOINT), capture(captureUserOrg),
         * eq(muleMessageProps))).andReturn(mockMuleMessage);
         */

        expectedUserOrg.setUserOrgId(1L);

        // user organization should be created, newUserOrg = true
        // userOrg is saved, succeeded = true
        mockEventPublisher.publishProfileEvent(expectedUserOrg, mockAuthentication, true, true);
        expectLastCall().once();

        replayAll();

        ProfileDTO profileDTO = userOrgService.getProfileInfo(USER_ID, mockAuthentication);

        verifyAll();

        assertEquals(captureUserOrg.getValue().getUser().getUserId(), USER_ID);
        assertEquals(profileDTO.getUserId(), expectedUserOrg.getUser().getUserId());
        assertEquals(profileDTO.getGroups().get(0), group.getName());
        assertNull(profileDTO.getCompanyName());
    }

    private void verifyUserOrgIsPopulated(UserOrg userOrg, ProfileDTO profileDTO)
    {
        assertEquals(userOrg.getFax(), profileDTO.getFax());
        assertEquals(userOrg.getWebsite(), profileDTO.getWebsite());
        assertEquals(userOrg.getZip(), profileDTO.getZip());
        assertEquals(userOrg.getCity(), profileDTO.getCity());
        assertEquals(userOrg.getState(), profileDTO.getState());
        assertEquals(userOrg.getFirstAddress(), profileDTO.getFirstAddress());
        assertEquals(userOrg.getSecondAddress(), profileDTO.getSecondAddress());
        assertEquals(userOrg.getImAccount(), profileDTO.getImAccount());
        assertEquals(userOrg.getImSystem(), profileDTO.getImSystem());
        assertEquals(userOrg.getLocation(), profileDTO.getLocation());
        assertEquals(userOrg.getOfficePhoneNumber(), profileDTO.getOfficePhoneNumber());
        assertEquals(userOrg.getMainOfficePhone(), profileDTO.getMainOfficePhone());
        assertEquals(userOrg.getMobilePhoneNumber(), profileDTO.getMobilePhoneNumber());
        assertEquals(userOrg.getEcmFileId(), profileDTO.getEcmFileId());
        assertEquals(userOrg.getEcmSignatureFileId(), profileDTO.getEcmSignatureFileId());
        assertEquals(userOrg.getTitle(), profileDTO.getTitle());
    }

    private ProfileDTO createMockProfileDTO()
    {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setCompanyName(COMPANY_NAME);
        profileDTO.setWebsite("unknown.com");
        profileDTO.setZip("1430");
        profileDTO.setCity("");
        profileDTO.setState("Neverland");
        profileDTO.setFirstAddress("address");
        profileDTO.setSecondAddress("what address");
        profileDTO.setFax("fax");
        profileDTO.setImAccount("imAccount");
        profileDTO.setImSystem("imSystem");
        profileDTO.setLocation("location");
        profileDTO.setOfficePhoneNumber("office phone-number");
        profileDTO.setMainOfficePhone("main office phone-number");
        profileDTO.setMobilePhoneNumber("mobile phone number");
        profileDTO.setEcmFileId(1L);
        profileDTO.setTitle("title");
        return profileDTO;
    }

}
