package com.armedia.acm.services.users.service.group;

/*-
 * #%L
 * ACM Service: Users
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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CHILD_ID_SS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CREATOR_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.DESCRIPTION_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.HIDDEN_B;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MEMBER_ID_SS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.MODIFIER_FULL_NAME_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.SUPERVISOR_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author riste.tutureski
 */
public class GroupToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmGroup>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private AcmGroupDao groupDao;
    private UserDao userDao;
    private SpringContextHolder springContextHolder;
    private AcmLdapSyncConfig acmLdapSyncConfig;

    @Override
    public List<AcmGroup> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getGroupDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmGroup in)
    {

        acmLdapSyncConfig = springContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).get(in.getDirectoryName() + "_sync");
        LOG.debug("Creating Solr advanced search document for Group.");

        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();

        solrDoc.setId(in.getName() + "-GROUP");
        solrDoc.setObject_id_s(in.getName());
        solrDoc.setObject_type_s("GROUP");
        solrDoc.setName(in.getName());
        solrDoc.setName_lcs(in.getName());
        solrDoc.setCreate_date_tdt(in.getCreated());
        solrDoc.setAuthor(in.getCreator());
        solrDoc.setCreator_lcs(in.getCreator());
        solrDoc.setModified_date_tdt(in.getModified());
        solrDoc.setModifier_lcs(in.getModifier());

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());
        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(AcmGroup in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TITLE_PARSEABLE, in.getName());
        additionalProperties.put(DESCRIPTION_PARSEABLE, in.getDescription());
        additionalProperties.put(STATUS_LCS, in.getStatus().name());

        additionalProperties.put("object_sub_type_s", in.getType().name());
        additionalProperties.put("ascendants_id_ss", in.getAscendantsStream().collect(Collectors.toList()));

        additionalProperties.put("groups_member_of_id_ss",
                in.getMemberOfGroups().stream().map(AcmGroup::getName).collect(Collectors.toList()));

        if (in.getSupervisor() != null)
        {
            additionalProperties.put(SUPERVISOR_ID_S, in.getSupervisor().getUserId());
            if (in.getSupervisor().getFullName() != null)
            {
                additionalProperties.put("supervisor_name_s", in.getSupervisor().getFullName());
            }
        }

        additionalProperties.put(MEMBER_ID_SS, in.getUserMemberIds().collect(Collectors.toList()));
        additionalProperties.put(CHILD_ID_SS, in.getGroupMemberNames().collect(Collectors.toList()));

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

        additionalProperties.put("directory_name_s", in.getDirectoryName());
        additionalProperties.put("name_partial", in.getName());

        // set hidden_b to true if group is group/user control group
        if (acmLdapSyncConfig != null)
        {
            if (in.getName().equalsIgnoreCase(acmLdapSyncConfig.getGroupControlGroup())
                    || in.getName().equalsIgnoreCase(acmLdapSyncConfig.getUserControlGroup()))
            {
                additionalProperties.put(HIDDEN_B, true);
            }
        }
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmGroup.class.equals(acmObjectType);
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
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
        return AcmGroup.class;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }
}
