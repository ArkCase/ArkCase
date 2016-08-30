package com.armedia.acm.plugins.profile.service;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.service.OrganizationService;
import com.armedia.acm.plugins.profile.dao.UserOrgDao;
import com.armedia.acm.plugins.profile.model.ProfileDTO;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.RoleType;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.*;


public class UserOrgServiceImplTest extends EasyMockSupport
{
    private static final String USER_ID = "ann-acm";
    private static final String COMPANY_NAME = "ARMEDIA";
    private static final String MULE_ENDPOINT = "vm://saveUserOrg.in";
    private static final String EXCEPTION_PROPERTY = "saveException";
    private UserDao mockUserDao;
    private UserOrgDao mockUserOrgDao;
    private OrganizationService mockOrganizationService;
    private UserOrgServiceImpl userOrgService;
    private Authentication mockAuthentication;
    private ProfileEventPublisher mockEventPublisher;
    private MuleContextManager mockMuleContextManager;
    private Map<String, Object> muleMessageProps;

    @Before
    public void setUp()
    {
        mockAuthentication = createMock(AcmAuthentication.class);
        mockUserDao = createMock(UserDao.class);
        mockUserOrgDao = createMock(UserOrgDao.class);
        mockOrganizationService = createMock(OrganizationService.class);
        mockEventPublisher = createMock(ProfileEventPublisher.class);
        mockMuleContextManager = createMock(MuleContextManager.class);

        userOrgService = new UserOrgServiceImpl();
        userOrgService.setUserDao(mockUserDao);
        userOrgService.setUserOrgDao(mockUserOrgDao);
        userOrgService.setOrganizationService(mockOrganizationService);
        userOrgService.setEventPublisher(mockEventPublisher);
        userOrgService.setMuleContextManager(mockMuleContextManager);

        muleMessageProps = Collections.singletonMap("acmUser", mockAuthentication);
    }

    @Test
    public void saveUserOrgInfoWhenCompanyNameIsWhiteSpaceOnlyAndUserOrgExists() throws MuleException
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

    private void expectWhenUpdateUserOrgSuccessfully(UserOrg userOrg) throws MuleException
    {
        expect(mockAuthentication.getName()).andReturn(USER_ID);
        expect(userOrgService.getUserOrgForUserId(USER_ID)).andReturn(userOrg);

        MuleMessage mockMuleMessage = createMock(MuleMessage.class);
        MuleException mockMuleException = null;

        expect(mockMuleContextManager.send(MULE_ENDPOINT, userOrg, muleMessageProps)).andReturn(mockMuleMessage);
        expect(mockMuleMessage.getPayload(UserOrg.class)).andReturn(userOrg);
        expect(mockMuleMessage.getInboundProperty(EXCEPTION_PROPERTY)).andReturn(mockMuleException);
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
    public void saveUserOrgInfoWhenCompanyNameNotBlankAndUserOrgExists() throws MuleException
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
    public void saveUserOrgInfoWhenCompanyNameNotBlankAndUserOrgNull() throws MuleException
    {
        ProfileDTO profileDTO = createMockProfileDTO();

        UserOrg expectedUserOrg = createAndSetUserOrg();
        expectedUserOrg.setUserOrgId(1L);

        expect(mockAuthentication.getName()).andReturn(USER_ID);
        expect(userOrgService.getUserOrgForUserId(USER_ID)).andReturn(null);
        expect(mockUserDao.findByUserId(USER_ID)).andReturn(expectedUserOrg.getUser());
        expect(mockOrganizationService.findOrCreateOrganization(COMPANY_NAME, USER_ID))
                .andReturn(expectedUserOrg.getOrganization());

        MuleMessage mockMuleMessage = createMock(MuleMessage.class);
        MuleException mockMuleException = null;

        Capture<UserOrg> captureUserOrg = Capture.newInstance();
        Capture<UserOrg> captureSavedUserOrg = Capture.newInstance();

        expect(mockMuleContextManager.send(eq(MULE_ENDPOINT), capture(captureUserOrg),
                eq(muleMessageProps))).andReturn(mockMuleMessage);

        expect(mockMuleMessage.getPayload(UserOrg.class)).andReturn(expectedUserOrg);

        expect(mockMuleMessage.getInboundProperty(EXCEPTION_PROPERTY)).andReturn(mockMuleException);

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
    public void saveUserOrgInfoWhenCompanyNameNotBlankUserOrgNullAndFailedToBeCreated() throws MuleException
    {
        ProfileDTO profileDTO = createMockProfileDTO();

        UserOrg expectedUserOrg = createAndSetUserOrg();
        expectedUserOrg.setUserOrgId(1L);

        expect(mockAuthentication.getName()).andReturn(USER_ID);
        expect(userOrgService.getUserOrgForUserId(USER_ID)).andReturn(null);
        expect(mockUserDao.findByUserId(USER_ID)).andReturn(expectedUserOrg.getUser());
        expect(mockOrganizationService.findOrCreateOrganization(COMPANY_NAME, USER_ID))
                .andReturn(expectedUserOrg.getOrganization());

        MuleMessage mockMuleMessage = createMock(MuleMessage.class);
        MuleException mockMuleException = createMock(MuleException.class);

        Capture<UserOrg> captureUserOrg = Capture.newInstance();

        expect(mockMuleContextManager.send(eq(MULE_ENDPOINT), capture(captureUserOrg),
                eq(muleMessageProps))).andReturn(mockMuleMessage);

        expect(mockMuleMessage.getPayload(UserOrg.class)).andReturn(null);

        expect(mockMuleMessage.getInboundProperty(EXCEPTION_PROPERTY)).andReturn(mockMuleException);

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

        AcmRole role = new AcmRole();
        role.setRoleName("ACM_INVESTIGATOR");
        List<AcmRole> roles = new ArrayList<>();
        roles.add(role);

        expect(mockUserOrgDao.getUserOrgForUserId(USER_ID)).andReturn(expectedUserOrg);
        expect(mockUserDao.findAllRolesByUserAndRoleType(USER_ID, RoleType.LDAP_GROUP)).andReturn(roles);

        replayAll();

        ProfileDTO profileDTO = userOrgService.getProfileInfo(USER_ID, mockAuthentication);

        verifyAll();

        assertEquals(expectedUserOrg.getWebsite(), profileDTO.getWebsite());
        assertEquals(expectedUserOrg.getUser().getUserId(), profileDTO.getUserId());
        assertEquals(role.getRoleName(), profileDTO.getGroups().get(0));
        assertEquals(expectedUserOrg.getOrganization().getOrganizationValue(), profileDTO.getCompanyName());
    }

    @Test
    public void getProfileInfoWhenUserOrgNull() throws MuleException
    {
        AcmUser user = new AcmUser();
        user.setUserId(USER_ID);
        UserOrg expectedUserOrg = new UserOrg();
        expectedUserOrg.setUser(user);

        AcmRole role = new AcmRole();
        role.setRoleName("ACM_INVESTIGATOR");
        List<AcmRole> roles = new ArrayList<>();
        roles.add(role);

        expect(mockUserOrgDao.getUserOrgForUserId(USER_ID)).andReturn(null);
        expect(mockUserDao.findByUserId(USER_ID)).andReturn(user);
        expect(mockUserDao.findAllRolesByUserAndRoleType(USER_ID, RoleType.LDAP_GROUP)).andReturn(roles);

        MuleMessage mockMuleMessage = createMock(MuleMessage.class);
        MuleException mockMuleException = null;

        Capture<UserOrg> captureUserOrg = Capture.newInstance();

        expect(mockMuleContextManager.send(eq(MULE_ENDPOINT), capture(captureUserOrg),
                eq(muleMessageProps))).andReturn(mockMuleMessage);

        expectedUserOrg.setUserOrgId(1L);

        expect(mockMuleMessage.getPayload(UserOrg.class)).andReturn(expectedUserOrg);
        expect(mockMuleMessage.getInboundProperty(EXCEPTION_PROPERTY)).andReturn(mockMuleException);

        // user organization should be created, newUserOrg = true
        // userOrg is saved, succeeded = true
        mockEventPublisher.publishProfileEvent(expectedUserOrg, mockAuthentication, true, true);
        expectLastCall().once();

        replayAll();

        ProfileDTO profileDTO = userOrgService.getProfileInfo(USER_ID, mockAuthentication);

        verifyAll();

        assertEquals(captureUserOrg.getValue().getUser().getUserId(), USER_ID);
        assertEquals(profileDTO.getUserId(), expectedUserOrg.getUser().getUserId());
        assertEquals(profileDTO.getGroups().get(0), role.getRoleName());
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
