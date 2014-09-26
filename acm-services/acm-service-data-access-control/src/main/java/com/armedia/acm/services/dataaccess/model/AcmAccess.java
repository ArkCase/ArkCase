package com.armedia.acm.services.dataaccess.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  acls for a business object
 */
public class AcmAccess {
    public static final String SOLR_ALLOW_ACL = "allow_acl_ss";
    public static final String SOLR_DENY_ACL = "deny_acl_ss";

    Long objectId;
    String objectType;
    String objectStatus;
    String accessLevel;

    Set<String> allowAcls = new HashSet();
    Set<String> denyAcls = new HashSet();

    public AcmAccess(Long objectId, String objectType, String objectStatus, String accessLevel) {
        this.objectId = objectId;
        this.objectType = objectType;
        this.objectStatus = objectStatus;
        this.accessLevel = accessLevel;
    }

    public void enrichWithAcls(Map<String, Object> map) {
        /**
         * @todo REMOVE - will not see acl fields in solr if empty so we
         * @todo are temporarily adding test ones until we get acls populated
         * @todo in db as expected
         */
        this.getDenyAcls().add("TEST-DENY-ACL");
        this.getAllowAcls().add("TEST-ALLOW-ACL");

        map.put(AcmAccess.SOLR_ALLOW_ACL, this.getAllowAcls());
        map.put(AcmAccess.SOLR_DENY_ACL, this.getDenyAcls());
    }

    public void addAllowAcl(String acl) {
        this.allowAcls.add(acl);
    }

    public void addDenyAcl(String acl) {
        this.denyAcls.add(acl);
    }

    public Set<String> getAllowAcls() {
        return allowAcls;
    }

    public Set<String> getDenyAcls() {
        return denyAcls;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public Long getObjectId() {
        return objectId;
    }
}
