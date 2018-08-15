package com.armedia.acm.services.users.service.group;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Spy
    private AcmGroup group;

    @Mock
    private AcmUser user;

    @Mock
    private AcmGroup mockedMemberGroup1;

    @Mock
    private AcmGroup mockedMemberSubGroup1;

    @Mock
    private AcmGroup mockedMemberGroup2;

    @Mock
    private AcmGroup mockedMemberSubGroup2;

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
        group.setMemberGroups(new HashSet<>(Arrays.asList(mockedMemberGroup1, mockedMemberGroup2)));
        when(group.getName()).thenReturn(GROUP);

        when(mockedMemberGroup1.getName()).thenReturn(GROUP_1);
        when(mockedMemberGroup1.getMemberGroups()).thenReturn(new HashSet<>(Arrays.asList(mockedMemberSubGroup1)));
        when(mockedMemberGroup1.getMemberOfGroups()).thenReturn(new HashSet<>(Arrays.asList(group)));

        when(mockedMemberGroup2.getName()).thenReturn(GROUP_2);
        when(mockedMemberGroup2.getMemberGroups()).thenReturn(new HashSet<>(Arrays.asList(mockedMemberSubGroup2)));
        when(mockedMemberGroup2.getMemberOfGroups()).thenReturn(new HashSet<>(Arrays.asList(group)));

        when(mockedMemberSubGroup1.getName()).thenReturn(SUBGROUP_1);
        when(mockedMemberSubGroup1.getMemberOfGroups()).thenReturn(new HashSet<>(Arrays.asList(mockedMemberGroup1)));

        when(mockedMemberSubGroup2.getName()).thenReturn(SUBGROUP_2);
        when(mockedMemberSubGroup2.getMemberOfGroups()).thenReturn(new HashSet<>(Arrays.asList(mockedMemberGroup2)));

        Set<AcmGroup> descendantGroups = AcmGroupUtils.findDescendantsForAcmGroup(group);
        Map<AcmGroup, String> ancestorStrings = descendantGroups.stream()
                .collect(Collectors.toMap(Function.identity(), AcmGroupUtils::buildAncestorsStringForAcmGroup));

        // when
        AcmGroup deletedGroup = groupService.markGroupDeleted(GROUP);

        // then
        verify(groupService).findByName(GROUP);
        assertThat(group.getMemberGroups().isEmpty(), is(true));
        verify(group).setAscendantsList(null);
        verify(group).setStatus(AcmGroupStatus.DELETE);
        verify(group).removeMembers();
        verify(mockedMemberGroup1).removeFromGroup(group);
        verify(mockedMemberGroup2).removeFromGroup(group);
        verify(group).setUserMembers(any(Set.class));
        verify(mockedGroupDao).deleteGroup(group);
        assertThat(group.getMemberGroups(), emptyCollectionOf(AcmGroup.class));

        ancestorStrings.forEach((group, ancestorString) -> {
            verify(group).setAscendantsList(ancestorString);
            verify(groupService).save(group);
        });

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
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#saveAdHocSubGroup(com.armedia.acm.services.users.model.group.AcmGroup, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test
    public void testSaveAdHocSubGroup_newSubGroup() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(group);
        when(group.getSupervisor()).thenReturn(user);
        when(group.getAscendantsList()).thenReturn("");
        when(mockedMemberGroup1.getSupervisor()).thenReturn(null);
        when(groupService.save(mockedMemberGroup1)).thenReturn(mockedMemberGroup1);

        // when
        AcmGroup resultGroup = groupService.saveAdHocSubGroup(mockedMemberGroup1, GROUP);

        // then
        verify(mockedMemberGroup1).setSupervisor(user);
        verify(mockedMemberGroup1).setAscendantsList("");
        verify(groupService).createGroup(mockedMemberGroup1);
        verify(groupService).save(mockedMemberGroup1);
        verify(group).addGroupMember(mockedMemberGroup1);

        assertThat(resultGroup, is(mockedMemberGroup1));
    }

    /**
     * Test method for
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#addGroupMember(java.lang.String, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test(expected = AcmCreateObjectFailedException.class)
    public void testSaveAdHocSubGroup_existingSubGroup_parentGroupNotFound() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP_1)).thenReturn(mockedMemberGroup1);

        try
        {
            // when
            groupService.addGroupMember(GROUP_1, GROUP);
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
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#addGroupMember(java.lang.String, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test(expected = AcmCreateObjectFailedException.class)
    public void testSaveAdHocSubGroup_existingSubGroup_subGroupNotFound() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(group);

        try
        {
            // when
            groupService.addGroupMember(GROUP_1, GROUP);
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
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#addGroupMember(java.lang.String, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test(expected = AcmCreateObjectFailedException.class)
    public void testSaveAdHocSubGroup_existingSubGroup_bothGroupsNotFound() throws Exception
    {
        try
        {
            // when
            groupService.addGroupMember(GROUP_1, GROUP);
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
     * {@link com.armedia.acm.services.users.service.group.GroupServiceImpl#addGroupMember(java.lang.String, java.lang.String)}.
     *
     * @throws Exception
     */
    @Test
    public void testSaveAdHocSubGroup_existingSubGroup() throws Exception
    {
        // given
        when(mockedGroupDao.findByName(GROUP)).thenReturn(group);
        when(mockedGroupDao.findByName(GROUP_1)).thenReturn(mockedMemberGroup1);

        when(group.getSupervisor()).thenReturn(user);
        when(mockedMemberGroup1.getSupervisor()).thenReturn(null);

        when(mockedMemberGroup1.getName()).thenReturn(GROUP_1);
        when(group.getName()).thenReturn(GROUP);

        when(group.getMemberOfGroups()).thenReturn(new HashSet<>());

        when(mockedMemberGroup1.getMemberOfGroups()).thenReturn(new HashSet<>(Arrays.asList(group)));
        when(mockedMemberGroup1.getMemberGroups()).thenReturn(new HashSet<>());

        // when
        AcmGroup resultGroup = groupService.addGroupMember(GROUP_1, GROUP);

        // then
        verify(group).getSupervisor();
        verify(mockedMemberGroup1).getSupervisor();
        verify(mockedMemberGroup1).setSupervisor(user);
        verify(group).addGroupMember(mockedMemberGroup1);
        verify(mockedMemberGroup1).setAscendantsList(eq(GROUP));

        assertThat(resultGroup, is(mockedMemberGroup1));
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
        try
        {
            // when
            groupService.saveAdHocSubGroup(mockedMemberGroup1, GROUP);
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
