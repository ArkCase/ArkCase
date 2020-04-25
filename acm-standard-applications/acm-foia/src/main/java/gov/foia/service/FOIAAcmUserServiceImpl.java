package gov.foia.service;

import static com.armedia.acm.services.users.model.ldap.MapperUtils.prefixTrailingDot;

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.users.model.AcmUsersConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.service.AcmUserServiceImpl;
import com.armedia.acm.spring.SpringContextHolder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

/**
 * Created by ana.serafimoska
 */
public class FOIAAcmUserServiceImpl extends AcmUserServiceImpl
{
    @Value("${foia.portalserviceprovider.directory.name}")
    private String directoryName;

    private SpringContextHolder acmContextHolder;

    private ExecuteSolrQuery executeSolrQuery;

    @Override
    public String getNUsers(Authentication auth, String sortBy, String sortDirection, int startRow, int maxRows)
            throws SolrException
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String userPrefix = prefixTrailingDot(ldapSyncConfig.getUserPrefix());
        String query = "object_type_s:USER AND status_lcs:VALID AND -object_id_s:" + userPrefix + "*";
        String filterSystemUsers = String.format("fq=-name:%s&fq=-name:%s", AcmUsersConstants.OCR_SYSTEM_USER,
                AcmUsersConstants.TRANSCRIBE_SYSTEM_USER);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                sortBy + " " + sortDirection, filterSystemUsers);
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    @Override
    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    @Override
    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}