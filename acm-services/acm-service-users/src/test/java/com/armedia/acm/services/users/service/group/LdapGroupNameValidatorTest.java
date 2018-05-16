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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupType;

import org.easymock.EasyMockSupport;
import org.junit.Test;

public class LdapGroupNameValidatorTest extends EasyMockSupport
{

    private LdapGroupNameValidator unit = new LdapGroupNameValidator();

    @Test
    public void invalidLdapGroupNameLength()
    {
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName("ACM_ADMINISTATOR_DEV_TEST_VALIDATE_LDAP_GROUP_NAME_LENGTH_TESTTTTTTTTTTTTTTTTTTTTTTTT@ARMEDIA.COM");
        acmGroup.setType(AcmGroupType.LDAP_GROUP);

        boolean res = unit.isValid(acmGroup, null);
        assertFalse(res);

    }

    @Test
    public void validLdapGroupNameLength()
    {
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName("ACM_ADMINISTATOR_DEV@ARMEDIA.COM");
        acmGroup.setType(AcmGroupType.LDAP_GROUP);

        boolean res = unit.isValid(acmGroup, null);
        assertTrue(res);
    }

    @Test
    public void undefinedLdapGroupType()
    {
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName("ACM_ADMINISTATOR_DEV@ARMEDIA.COM");
        acmGroup.setType(null);

        boolean res = unit.isValid(acmGroup, null);
        assertFalse(res);

    }

}
