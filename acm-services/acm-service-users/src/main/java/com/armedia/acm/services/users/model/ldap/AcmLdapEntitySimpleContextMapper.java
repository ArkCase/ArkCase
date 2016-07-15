package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.AcmUser;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;


public class AcmLdapEntitySimpleContextMapper implements ContextMapper
{
    private String userIdAttributeName;

    private String mailAttributeName;

    @Override
    public AcmLdapEntity mapFromContext(Object ctx)
    {
        DirContextAdapter adapter = (DirContextAdapter) ctx;
        return generateAcmUser(adapter);
    }


    private AcmUser generateAcmUser(DirContextAdapter adapter)
    {
        AcmUser retval = new AcmUser();
        retval.setFullName(adapter.getStringAttribute("cn"));
        if (adapter.attributeExists("sn"))
        {
            retval.setLastName(adapter.getStringAttribute("sn"));
        }
        if (adapter.attributeExists("givenName"))
        {
            retval.setFirstName(adapter.getStringAttribute("givenName"));
        }
        retval.setUserId(adapter.getStringAttribute(getUserIdAttributeName()));
        retval.setMail(adapter.getStringAttribute(getMailAttributeName()));
        retval.setDistinguishedName(adapter.getStringAttribute("dn") != null ? adapter.getStringAttribute("dn") : adapter.getStringAttribute("distinguishedname"));
        return retval;
    }

    public String getUserIdAttributeName()
    {
        return userIdAttributeName;
    }

    public void setUserIdAttributeName(String userIdAttributeName)
    {
        this.userIdAttributeName = userIdAttributeName;
    }

    public String getMailAttributeName()
    {
        return mailAttributeName;
    }

    public void setMailAttributeName(String mailAttributeName)
    {
        this.mailAttributeName = mailAttributeName;
    }
}
