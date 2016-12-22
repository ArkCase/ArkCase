package com.armedia.acm.plugins.addressable.service;

import com.armedia.acm.plugins.addressable.dao.ContactMethodDao;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/27/14.
 */
public class ContactMethodToSolrTransformer implements AcmObjectToSolrDocTransformer<ContactMethod>
{
    private ContactMethodDao contactMethodDao;
    private UserDao userDao;

    @Override
    public List<ContactMethod> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getContactMethodDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(ContactMethod cm)
    {
        SolrAdvancedSearchDocument cmDoc = new SolrAdvancedSearchDocument();
        cmDoc.setId(cm.getId() + "-CONTACT-METHOD");
        cmDoc.setObject_type_s("CONTACT-METHOD");
        cmDoc.setObject_id_s(cm.getId() + "");
        cmDoc.setType_lcs(cm.getType());
        cmDoc.setValue_parseable(cm.getValue());
        cmDoc.setCreate_date_tdt(cm.getCreated());
        cmDoc.setCreator_lcs(cm.getCreator());
        cmDoc.setModified_date_tdt(cm.getModified());
        cmDoc.setModifier_lcs(cm.getModifier());

        cmDoc.setName(cm.getValue());
        cmDoc.setTitle_parseable(cm.getValue());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(cm.getCreator());
        if (creator != null)
        {
            cmDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(cm.getModifier());
        if (modifier != null)
        {
            cmDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return cmDoc;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(ContactMethod in)
    {
        // No implementation needed
        return null;
    }

    // No implementation needed because we don't want ContactMethod indexed in the SolrQuickSearch
    @Override
    public SolrDocument toSolrQuickSearch(ContactMethod in)
    {
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return ContactMethod.class.equals(acmObjectType);
    }

    public ContactMethodDao getContactMethodDao()
    {
        return contactMethodDao;
    }

    public void setContactMethodDao(ContactMethodDao contactMethodDao)
    {
        this.contactMethodDao = contactMethodDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return ContactMethod.class;
    }
}
