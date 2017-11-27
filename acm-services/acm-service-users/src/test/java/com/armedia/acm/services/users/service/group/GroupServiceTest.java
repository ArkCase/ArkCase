package com.armedia.acm.services.users.service.group;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.service.AcmUserRoleService;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Nov 23, 2017
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupServiceTest
{

    /**
     *
     */
    private static final String USER_SUB_GROUP_2_ID = "USER_SUB_GROUP_2_ID";

    /**
     *
     */
    private static final String SUBGROUP_2 = "SUBGROUP_2";

    /**
     *
     */
    private static final String SUBGROUP_1 = "SUBGROUP_1";

    /**
     *
     */
    private static final String GROUP_2 = "GROUP_2";

    /**
     *
     */
    private static final String GROUP_1 = "GROUP_1";

    private static final String GROUP = "GROUP";

    @Mock
    private AcmGroupDao mockedGroupDao;

    @Mock
    private AcmUserRoleService mockedUserRoleService;

    @Spy
    private AcmGroup group;

    @Mock
    private AcmUser userGroup;

    @Mock
    private AcmGroup mockedMemeberOfGroup1;

    @Mock
    private AcmGroup mockedMemeberOfGroup2;

    @Mock
    private AcmGroup mockedMemeberGroup1;

    @Mock
    private AcmUser userGroup1;

    @Mock
    private AcmGroup mockedMemeberSubGroup1;

    @Mock
    private AcmUser userSubGroup1;

    @Mock
    private AcmGroup mockedMemeberGroup2;

    @Mock
    private AcmUser userGroup2;

    @Mock
    private AcmGroup mockedMemeberSubGroup2;

    @Mock
    private AcmUser userSubGroup2;

    @InjectMocks
    @Spy
    private GroupServiceImpl groupService;

    /**
     * Test method for
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#markGroupDeleted(java.lang.String)}.
     *
     * @throws Exception
     */
    @Test
    public void testMarkGroupDeleted_existingGroup() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(group);
        group.setMemberGroups(new HashSet<>(Arrays.asList(mockedMemeberGroup1, mockedMemeberGroup2)));
        when(group.getName()).thenReturn(GROUP);

        when(mockedMemeberGroup1.getName()).thenReturn(GROUP_1);
        when(mockedMemeberGroup1.getUserMembers()).thenReturn(new HashSet<>(Arrays.asList(userGroup1)));
        when(mockedMemeberGroup1.getMemberGroups()).thenReturn(new HashSet<>(Arrays.asList(mockedMemeberSubGroup1)));
        when(mockedMemeberGroup1.getMemberOfGroups()).thenReturn(new HashSet<>(Arrays.asList(group)));

        when(mockedMemeberGroup2.getName()).thenReturn(GROUP_2);
        when(mockedMemeberGroup2.getUserMembers()).thenReturn(new HashSet<>(Arrays.asList(userGroup2)));
        when(mockedMemeberGroup2.getMemberGroups()).thenReturn(new HashSet<>(Arrays.asList(mockedMemeberSubGroup2)));
        when(mockedMemeberGroup2.getMemberOfGroups()).thenReturn(new HashSet<>(Arrays.asList(group)));

        when(mockedMemeberSubGroup1.getName()).thenReturn(SUBGROUP_1);
        when(mockedMemeberSubGroup1.getUserMembers()).thenReturn(new HashSet<>(Arrays.asList(userSubGroup1)));
        when(mockedMemeberSubGroup1.getMemberOfGroups()).thenReturn(new HashSet<>(Arrays.asList(mockedMemeberGroup1)));

        when(mockedMemeberSubGroup2.getName()).thenReturn(SUBGROUP_2);
        when(mockedMemeberSubGroup2.getUserMembers()).thenReturn(new HashSet<>(Arrays.asList(userSubGroup2)));
        when(mockedMemeberSubGroup2.getMemberOfGroups()).thenReturn(new HashSet<>(Arrays.asList(mockedMemeberGroup2)));

        Set<AcmGroup> descendantGroups = AcmGroupUtils.findDescendantsForAcmGroup(group);
        Map<AcmGroup, String> ancestorStrings = descendantGroups.stream()
                .collect(Collectors.toMap(Function.identity(), group -> AcmGroupUtils.buildAncestorsStringForAcmGroup(group)));

        // when
        AcmGroup deletedGroup = groupService.markGroupDeleted(GROUP);

        // then
        verify(groupService).findByName(GROUP);
        verify(group).removeAsMemberOf();
        verify(group).isNotMemeberOfGroups();
        assertThat(group.isNotMemeberOfGroups(), is(true));
        verify(group).setAscendantsList(null);
        verify(group).setStatus(AcmGroupStatus.DELETE);
        verify(group).removeMembers();
        verify(mockedMemeberGroup1).removeFromGroup(group);
        verify(mockedMemeberGroup2).removeFromGroup(group);
        verify(group).setUserMembers(any(Set.class));
        verify(groupService).save(group);
        assertThat(group.getMemberGroups(), emptyCollectionOf(AcmGroup.class));

        ancestorStrings.forEach((group, ancestorString) -> {
            verify(group).setAscendantsList(ancestorString);
            verify(groupService).save(group);
        });

        List<AcmUser> users = Arrays.asList(userGroup1, userGroup2, userSubGroup1, userSubGroup2);
        Set<AcmGroup> groups = new HashSet<>(Arrays.asList(group));
        users.forEach(user -> verify(mockedUserRoleService).saveInvalidUserRolesPerRemovedUserGroups(eq(user), eq(groups)));

        assertThat(deletedGroup, is(group));
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#markGroupDeleted(java.lang.String)}.
     *
     * @throws Exception
     */
    @Test(expected = AcmObjectNotFoundException.class)
    public void testMarkGroupDeleted_nonExistingGroup() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(null);

        try
        {
            // when
            groupService.markGroupDeleted(GROUP);
        }
        catch (AcmObjectNotFoundException e)
        {
            // then
            verify(groupService).findByName(GROUP);
            assertThat(e.getObjectType(), is(GROUP));
            assertThat(e.getMessage(), is(
                    "Server encountered exception: Group with name GROUP not found\nException type was: 'com.armedia.acm.core.exceptions.AcmObjectNotFoundException'."));
            throw e;
        }
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#saveAdHocSubGroup(java.lang.String, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test
    public void testSaveAdHocSubGroup_existingSubGroup() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(group);
        when(group.getSupervisor()).thenReturn(userGroup);
        Stream<String> parentAscendants = Stream.empty();
        when(group.getAscendants()).thenReturn(parentAscendants);
        when(mockedGroupDao.findByName(GROUP_1)).thenReturn(mockedMemeberGroup1);
        when(mockedMemeberGroup1.getSupervisor()).thenReturn(null);
        when(mockedMemeberGroup1.getMemberGroups()).thenReturn(new HashSet<>(Arrays.asList(mockedMemeberSubGroup2)));
        when(mockedMemeberSubGroup2.getUserMembers()).thenReturn(new HashSet<>(Arrays.asList(userSubGroup2)));
        when(userSubGroup2.getUserId()).thenReturn(USER_SUB_GROUP_2_ID);

        // when
        AcmGroup resultGroup = groupService.saveAdHocSubGroup(GROUP_1, GROUP);

        // then
        verify(mockedMemeberGroup1).setSupervisor(userGroup);
        verify(mockedMemeberGroup1).addAscendants(parentAscendants);
        verify(group).addGroupMember(mockedMemeberGroup1);
        verify(mockedUserRoleService).saveValidUserRolesPerAddedUserGroups(USER_SUB_GROUP_2_ID, new HashSet<>(Arrays.asList(group)));

        assertThat(resultGroup, is(mockedMemeberGroup1));
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#saveAdHocSubGroup(java.lang.String, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test(expected = AcmCreateObjectFailedException.class)
    public void testSaveAdHocSubGroup_existingSubGroup_parentGroupNotFound() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(null);
        when(mockedGroupDao.findByName(GROUP_1)).thenReturn(mockedMemeberGroup1);

        try
        {
            // when
            groupService.saveAdHocSubGroup(GROUP_1, GROUP);
        }
        catch (AcmCreateObjectFailedException e)
        {
            // then
            verify(mockedGroupDao).findByName(GROUP);
            verify(mockedGroupDao).findByName(GROUP_1);
            assertThat(e.getObjectType(), is(GROUP));
            assertThat(e.getMessage(), is(
                    "Could not create GROUP.\nServer encountered exception: Parent group with id [GROUP] not found.\nException type was: 'com.armedia.acm.core.exceptions.AcmCreateObjectFailedException'."));
            throw e;
        }
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#saveAdHocSubGroup(java.lang.String, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test(expected = AcmCreateObjectFailedException.class)
    public void testSaveAdHocSubGroup_existingSubGroup_subGroupNotFound() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(group);
        when(mockedGroupDao.findByName(GROUP_1)).thenReturn(null);

        try
        {
            // when
            groupService.saveAdHocSubGroup(GROUP_1, GROUP);
        }
        catch (AcmCreateObjectFailedException e)
        {
            // then
            verify(mockedGroupDao).findByName(GROUP);
            verify(mockedGroupDao).findByName(GROUP_1);
            assertThat(e.getObjectType(), is(GROUP));
            assertThat(e.getMessage(), is(
                    "Could not create GROUP.\nServer encountered exception: Subgroup with id [GROUP_1] not found.\nException type was: 'com.armedia.acm.core.exceptions.AcmCreateObjectFailedException'."));
            throw e;
        }
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#saveAdHocSubGroup(java.lang.String, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test(expected = AcmCreateObjectFailedException.class)
    public void testSaveAdHocSubGroup_existingSubGroup_bothGroupsNotFound() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(null);
        when(mockedGroupDao.findByName(GROUP_1)).thenReturn(null);

        try
        {
            // when
            groupService.saveAdHocSubGroup(GROUP_1, GROUP);
        }
        catch (AcmCreateObjectFailedException e)
        {
            // then
            verify(mockedGroupDao).findByName(GROUP);
            verify(mockedGroupDao).findByName(GROUP_1);
            assertThat(e.getObjectType(), is(GROUP));
            assertThat(e.getMessage(), is(
                    "Could not create GROUP.\nServer encountered exception: Parent group with id [GROUP] not found. Subgroup with id [GROUP_1] not found.\nException type was: 'com.armedia.acm.core.exceptions.AcmCreateObjectFailedException'."));
            throw e;
        }
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#saveAdHocSubGroup(com.armedia.acm.services.users.model.group.AcmGroup, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test
    public void testSaveAdHocSubGroup_newSubGroup() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(group);
        when(group.getSupervisor()).thenReturn(userGroup);
        when(group.getAscendantsList()).thenReturn("");

        when(mockedMemeberGroup1.getName()).thenReturn(GROUP_1);

        // when
        AcmGroup resultGroup = groupService.saveAdHocSubGroup(mockedMemeberGroup1, GROUP);

        // then
        verify(mockedMemeberGroup1).getSupervisor();
        verify(group).getSupervisor();
        verify(mockedMemeberGroup1).setSupervisor(userGroup);
        verify(mockedMemeberGroup1).setAscendantsList(eq(""));
        verify(mockedMemeberGroup1).addAscendant(GROUP);
        verify(mockedMemeberGroup1).setName(startsWith(GROUP_1 + "-UUID-"));
        verify(group).addGroupMember(mockedMemeberGroup1);

        assertThat(resultGroup, is(mockedMemeberGroup1));
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#saveAdHocSubGroup(com.armedia.acm.services.users.model.group.AcmGroup, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test(expected = AcmCreateObjectFailedException.class)
    public void testSaveAdHocSubGroup_newSubGroup_parentNotFound() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(null);

        try
        {
            // when
            groupService.saveAdHocSubGroup(mockedMemeberGroup1, GROUP);
        }
        catch (AcmCreateObjectFailedException e)
        {
            // then
            verify(mockedGroupDao).findByName(GROUP);
            assertThat(e.getObjectType(), is(GROUP));
            assertThat(e.getMessage(), is(
                    "Could not create GROUP.\nServer encountered exception: Parent group with id [GROUP] not found\nException type was: 'com.armedia.acm.core.exceptions.AcmCreateObjectFailedException'."));
            throw e;
        }
    }

}
