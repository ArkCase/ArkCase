/**
 *
 */
package com.armedia.acm.services.users.service.group;

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.GroupConstants;
import org.mule.api.MuleException;
import org.mule.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author riste.tutureski
 */
public class GroupServiceImpl implements GroupService
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private UserDao userDao;
    private AcmGroupDao groupDao;
    private ExecuteSolrQuery executeSolrQuery;

    private Pattern pattern = Pattern.compile(GroupConstants.UUID_REGEX_STRING);

    @Override
    public AcmGroup updateGroupWithMembers(AcmGroup group, Set<AcmUser> members)
    {
        if (members != null)
        {
            for (AcmUser member : members)
            {
                group.addMember(member);
            }
        }

        return group;
    }

    @Override
    public Set<AcmUser> updateMembersWithDatabaseInfo(Set<AcmUser> members)
    {
        Set<AcmUser> updatedMembers = new HashSet<>();

        if (members != null)
        {
            for (AcmUser member : members)
            {
                AcmUser updatedMember = getUserDao().findByUserId(member.getUserId());

                if (updatedMember != null)
                {
                    updatedMembers.add(updatedMember);
                }
            }
        }

        return updatedMembers;
    }

    @Override
    public String getLdapGroupsForUser(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws MuleException
    {

        logger.info("Taking all groups and subgroups from Solr. Authenticated user is {}", usernamePasswordAuthenticationToken.getName());

        String query = "object_type_s:GROUP AND object_sub_type_s:LDAP_GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        String queryResults = getExecuteSolrQuery().getResultsByPredefinedQuery(usernamePasswordAuthenticationToken,
                SolrCore.ADVANCED_SEARCH, query, 0, 1000, "name asc");

        return queryResults;

    }


    @Override
    public AcmGroup checkAndSaveAdHocGroup(AcmGroup group)
    {
        boolean isGroupNameTaken = isGroupUINameTakenOnASameTreeLevel(group);

        if (!isGroupNameTaken)
        {
            group.setName(group.getName() + "-UUID-" + UUID.getUUID());
            return getGroupDao().save(group);
        } else
        {
            return null;
        }
    }

    private boolean isGroupUINameTakenOnASameTreeLevel(AcmGroup group)
    {
        AcmGroup g = group.getParentGroup() != null ? groupDao.subGroupByUIName(group) : groupDao.groupByUIName(group);
        return g != null && isUUIDPresentInTheGroupName(g.getName()) ? true : false;
    }

    @Override
    public boolean isUUIDPresentInTheGroupName(String str)
    {
        return pattern.matcher(str).matches();
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
