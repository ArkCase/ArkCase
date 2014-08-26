package com.armedia.acm.services.dataaccess.model;


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AcmAccessTest {
    @Test
    public void allowAclAdded() {
        AcmAccess acmAccess = new AcmAccess(1L,"","", "");
        acmAccess.addAllowAcl("1");
        Assert.assertTrue(acmAccess.getAllowAcls().contains("1"));
    }

    @Test
    public void denyAclAdded() {
        AcmAccess acmAccess = new AcmAccess(1L,"","", "");
        acmAccess.addDenyAcl("1");
        Assert.assertTrue(acmAccess.getDenyAcls().contains("1"));
    }

    @Test
    public void mapEnrichedWithAcls() {
        AcmAccess acmAccess = new AcmAccess(1L,"","", "");
        Map m = new HashMap();
        acmAccess.enrichWithAcls(m);
        Assert.assertTrue(m.containsKey(AcmAccess.SOLR_ALLOW_ACL));
        Assert.assertTrue(m.containsKey(AcmAccess.SOLR_DENY_ACL));
        Assert.assertTrue(m.get(AcmAccess.SOLR_DENY_ACL) instanceof Set);
        Assert.assertTrue(m.get(AcmAccess.SOLR_ALLOW_ACL) instanceof Set);
    }

}
