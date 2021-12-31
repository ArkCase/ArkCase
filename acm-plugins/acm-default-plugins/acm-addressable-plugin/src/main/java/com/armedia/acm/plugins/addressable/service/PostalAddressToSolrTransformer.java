package com.armedia.acm.plugins.addressable.service;

/*-
 * #%L
 * ACM Default Plugin: Addressable
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.LOCATION_CITY_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.LOCATION_POSTAL_CODE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.LOCATION_STATE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.LOCATION_STREET_ADDRESS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;

import com.armedia.acm.plugins.addressable.dao.PostalAddressDao;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 10/27/14.
 */
public class PostalAddressToSolrTransformer implements AcmObjectToSolrDocTransformer<PostalAddress>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private PostalAddressDao postalAddressDao;
    private UserDao userDao;

    @Override
    public List<PostalAddress> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getPostalAddressDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(PostalAddress in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for LOCATION.");

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(),
                in.getModified(), "LOCATION", buildName(in));

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(PostalAddress in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(LOCATION_CITY_LCS, in.getCity());
        additionalProperties.put(LOCATION_POSTAL_CODE_LCS, in.getZip());
        additionalProperties.put(LOCATION_STATE_LCS, in.getState());
        additionalProperties.put(LOCATION_STREET_ADDRESS_LCS, in.getStreetAddress());
        additionalProperties.put(TITLE_PARSEABLE, buildName(in));

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            additionalProperties.put(CREATOR_FULL_NAME_LCS, creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            additionalProperties.put(MODIFIER_FULL_NAME_LCS, modifier.getFirstName() + " " + modifier.getLastName());
        }
    }

    private String buildName(PostalAddress address)
    {
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

        return name.toString();
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
