package com.armedia.acm.services.users.model;

/**
 * Created by armdev on 7/2/14.
 */
public interface AcmLdapEntity
{
    String getDistinguishedName();
    void setDistinguishedName(String distinguishedName);

    boolean isGroup();

}
