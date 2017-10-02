package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapGroupNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.Every.everyItem;

public class LdapGroupUtilsTest
{
    private LdapGroup a;
    private LdapGroup b;
    private LdapGroup c;
    private LdapGroup b1;
    private LdapGroup b2;
    private LdapGroup c1;
    private LdapGroup c2;

    private LdapGroupUtils ldapLdapGroupUtils;
    private LdapGroupNode ldapGroupNode;

    @Before
    public void setup()
    {
        a = ldapGroup("A");
        b = ldapGroup("B");
        b1 = ldapGroup("B1");
        b2 = ldapGroup("B2");
        c = ldapGroup("C");
        c1 = ldapGroup("C1");
        c2 = ldapGroup("C2");

        // Graph representation
        // @formatter:off
        /**
         * A ->
         *  B ->
         *   B1
         *   B2
         *  C ->
         *   C1 ->
         *    B1
         *   C2 ->
         *    A
         */
        // @formatter:on
        a.setMemberGroups(groups(b, c));
        b.setMemberGroups(groups(b1, b2));
        c.setMemberGroups(groups(c1, c2));
        c1.setMemberGroups(groups(b1));
        c2.setMemberGroups(groups(a));
    }

    @Test
    public void findDescendantsOfATest()
    {
        ldapGroupNode = new LdapGroupNode(a);
        ldapLdapGroupUtils = new LdapGroupUtils();
        Set<LdapGroup> visited = ldapLdapGroupUtils.findDescendantsForLdapGroupNode(ldapGroupNode);

        assertThat("Visited should be", visited, everyItem(isIn(groups(a, b, c, b1, b2, c1, c2))));
    }

    @Test
    public void testFindAscendantsOfATest()
    {
        ldapGroupNode = new LdapGroupNode(a);
        ldapLdapGroupUtils = new LdapGroupUtils();
        Set<LdapGroup> visited = ldapLdapGroupUtils.findAscendantsForLdapGroupNode(ldapGroupNode, groups(a, b, b1, b2, c, c1, c2));
        assertThat("Ascendants of A should be", visited, everyItem(isIn(groups(c2, c))));
    }

    private Set<LdapGroup> groups(LdapGroup... groups)
    {
        return new HashSet<>(Arrays.asList(groups));
    }

    private LdapGroup ldapGroup(String name)
    {
        LdapGroup ldapGroup = new LdapGroup();
        ldapGroup.setName(name);
        return ldapGroup;
    }
}
