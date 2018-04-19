package com.armedia.acm.services.users.service.group;

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class LdapGroupNameValidatorTest extends EasyMockSupport {

    private LdapGroupNameValidator unit = new LdapGroupNameValidator();


    @Test
    public void invalidLdapGroupNameLength(){
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName("ACM_ADMINISTATOR_DEV_TEST_VALIDATE_LDAP_GROUP_NAME_LENGTH_TESTTTTTTTTTTTTTTTTTTTTTTTT@ARMEDIA.COM");
        acmGroup.setType(AcmGroupType.LDAP_GROUP);

        boolean res = unit.isValid(acmGroup, null);
        assertFalse(res);

    }

    @Test
    public void validLdapGroupNameLength(){
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName("ACM_ADMINISTATOR_DEV@ARMEDIA.COM");
        acmGroup.setType(AcmGroupType.LDAP_GROUP);

        boolean res = unit.isValid(acmGroup, null);
        assertTrue(res);
    }

    @Test
    public void undefinedLdapGroupType(){
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName("ACM_ADMINISTATOR_DEV@ARMEDIA.COM");
        acmGroup.setType(null);

        boolean res = unit.isValid(acmGroup, null);
        assertFalse(res);

    }

}
