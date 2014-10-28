package com.armedia.acm.plugins.addressable.service;

import com.armedia.acm.plugins.addressable.dao.ContactMethodDao;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 10/27/14.
 */
public class ContactMethodToSolrTransformer implements AcmObjectToSolrDocTransformer<ContactMethod>
{
    private ContactMethodDao contactMethodDao;

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

        return cmDoc;
    }

    @Override
    public SolrDocument toSolrQuickSearch(ContactMethod in)
    {
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        boolean objectNotNull = acmObjectType != null;
        String ourClassName = ContactMethod.class.getName();
        String theirClassName = acmObjectType.getName();
        boolean classNames = theirClassName.equals(ourClassName);
        boolean isSupported = objectNotNull && classNames;

        return isSupported;
    }

    public ContactMethodDao getContactMethodDao()
    {
        return contactMethodDao;
    }

    public void setContactMethodDao(ContactMethodDao contactMethodDao)
    {
        this.contactMethodDao = contactMethodDao;
    }
}
