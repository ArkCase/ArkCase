package gov.foia.service;

import static com.armedia.acm.services.users.model.ldap.MapperUtils.prefixTrailingDot;

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.service.group.GroupServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import java.util.Optional;

/**
 * Created by ana.serafimoska
 */
public class FOIAGroupServiceImpl extends GroupServiceImpl
{
    @Value("${foia.portalserviceprovider.directory.name}")
    private String directoryName;
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public String getUserMembersForGroup(String groupName, Optional<String> userStatus, Authentication auth) throws SolrException
    {
        AcmLdapSyncConfig ldapSyncConfiguration = getSpringContextHolder().getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String userPrefix = prefixTrailingDot(ldapSyncConfiguration.getUserPrefix());

        String statusQuery = userStatus.map(it -> {
            try
            {
                AcmUserState state = AcmUserState.valueOf(it);
                return String.format(" AND status_lcs:%s", state);
            }
            catch (IllegalArgumentException e)
            {
                log.debug("usersStatus: [{}] is not a valid value. Won't be included in the query!", userStatus);
                return "";
            }
        }).orElse("");

        String query = String.format("object_type_s:USER AND -object_id_s:" + userPrefix + "*" + " AND groups_id_ss:%s",
                buildSafeGroupNameForSolrSearch(groupName));
        query += statusQuery;

        log.debug("Executing query for users in group: [{}]", query);
        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, 1000, "");

    }

}