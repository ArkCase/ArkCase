package com.armedia.acm.plugins.addressable.service;

import com.armedia.acm.plugins.addressable.dao.PostalAddressDao;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
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
public class PostalAddressToSolrTransformer implements AcmObjectToSolrDocTransformer<PostalAddress>
{
    private PostalAddressDao postalAddressDao;
    private UserDao userDao;

    @Override
    public List<PostalAddress> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPostalAddressDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PostalAddress address)
    {
        SolrAdvancedSearchDocument addrDoc = new SolrAdvancedSearchDocument();
        addrDoc.setId(address.getId() + "-LOCATION");
        addrDoc.setObject_type_s("LOCATION");
        addrDoc.setObject_id_s(address.getId() + "");
        addrDoc.setLocation_city_lcs(address.getCity());
        addrDoc.setLocation_postal_code_sdo(address.getZip());
        addrDoc.setLocation_state_lcs(address.getState());
        addrDoc.setLocation_street_address_lcs(address.getStreetAddress());
        addrDoc.setCreate_date_tdt(address.getCreated());
        addrDoc.setCreator_lcs(address.getCreator());
        addrDoc.setModified_date_tdt(address.getModified());
        addrDoc.setModifier_lcs(address.getModifier());

        StringBuilder name = new StringBuilder();
        boolean hasAddress1 = address.getStreetAddress() != null;
        boolean hasAddress2 = address.getStreetAddress2() != null;
        boolean hasCity = address.getCity() != null;
        boolean hasState = address.getState() != null;
        boolean hasZip = address.getZip() != null;

        name.append(hasAddress1 ? address.getStreetAddress() : "");
        name.append(hasAddress1 && (hasAddress2 || hasCity || hasState) ? ", " : "");
        name.append(hasAddress2 ? address.getStreetAddress2() : "");
        name.append(hasAddress2 && (hasCity || hasState) ? ", " : "");
        name.append(hasCity ? address.getCity() : "");
        name.append(hasCity && hasState ? ", " : "");
        name.append(hasState ? address.getState() : "");
        name.append(hasZip ? "  " : "");
        name.append(hasZip ? address.getZip() : "");

        addrDoc.setName(name.toString());

        addrDoc.setTitle_parseable(name.toString());

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(address.getCreator());
        if (creator != null)
        {
            addrDoc.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(address.getModifier());
        if (modifier != null)
        {
            addrDoc.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        return addrDoc;

    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(PostalAddress in)
    {
        // No implementation needed
        return null;
    }

    // No implementation needed because we don't want PostalAddress indexed in the SolrQuickSearch
    @Override
    public SolrDocument toSolrQuickSearch(PostalAddress in)
    {
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return PostalAddress.class.equals(acmObjectType);
    }

    public PostalAddressDao getPostalAddressDao()
    {
        return postalAddressDao;
    }

    public void setPostalAddressDao(PostalAddressDao postalAddressDao)
    {
        this.postalAddressDao = postalAddressDao;
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
        return PostalAddress.class;
    }
}
