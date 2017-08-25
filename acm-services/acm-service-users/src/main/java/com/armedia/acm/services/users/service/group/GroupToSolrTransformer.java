package com.armedia.acm.services.users.service.group;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author riste.tutureski
 */
public class GroupToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmGroup>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private AcmGroupDao groupDao;
    private UserDao userDao;
    private GroupService groupService;

    @Override
    public List<AcmGroup> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getGroupDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmGroup in)
    {
        LOG.info("Creating Solr advanced search document for Group.");

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();
        solr.setId(in.getName() + "-GROUP");
        solr.setObject_id_s(in.getName());
        solr.setObject_type_s("GROUP");
        solr.setTitle_parseable(in.getName());
        if (groupService.isUUIDPresentInTheGroupName(in.getName()))
        {
            solr.setName(in.getName().substring(0, in.getName().lastIndexOf("-UUID-")));
        } else
        {
            solr.setName(in.getName());
        }
        solr.setDescription_parseable(in.getDescription());
        solr.setObject_sub_type_s(in.getType().name());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setStatus_lcs(in.getStatus().name());

        solr.getAdditionalProperties().put("ascendants_id_ss", in.getAscendants().collect(Collectors.toList()));

        if (in.getSupervisor() != null)
        {
            solr.setSupervisor_id_s(in.getSupervisor().getUserId());
            if (in.getSupervisor().getFullName() != null)
            {
                solr.getAdditionalProperties().put("supervisor_name_s", in.getSupervisor().getFullName());
            }
        }
        // TODO: also add descendants groups
        solr.setMember_id_ss(in.getUserMemberIds().collect(Collectors.toList()));

        /** Additional properties for full names instead of ID's */
        AcmUser creator = getUserDao().quietFindByUserId(in.getCreator());
        if (creator != null)
        {
            solr.setAdditionalProperty("creator_full_name_lcs", creator.getFirstName() + " " + creator.getLastName());
        }

        AcmUser modifier = getUserDao().quietFindByUserId(in.getModifier());
        if (modifier != null)
        {
            solr.setAdditionalProperty("modifier_full_name_lcs", modifier.getFirstName() + " " + modifier.getLastName());
        }

        solr.setAdditionalProperty("directory_name_s", in.getDirectoryName());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmGroup in)
    {
        LOG.info("Creating Solr quick search document for Group.");

        SolrDocument solr = new SolrDocument();
        solr.setId(in.getName() + "-GROUP");
        solr.setObject_id_s(in.getName());
        solr.setObject_type_s("GROUP");
        if (groupService.isUUIDPresentInTheGroupName(in.getName()))
        {
            solr.setName(in.getName().substring(0, in.getName().lastIndexOf("-UUID-")));
        } else
        {
            solr.setName(in.getName());
        }

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setTitle_parseable(in.getName());
        solr.setStatus_s(in.getStatus().name());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmGroup in)
    {
        // No implementation needed
        return null;
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

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmGroup.class;
    }
}
