package gov.foia.transformer;

import static com.armedia.acm.services.users.model.ldap.MapperUtils.prefixTrailingDot;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.service.ldap.UserToSolrTransformer;
import com.armedia.acm.spring.SpringContextHolder;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by ana.serafimoska
 */
public class FOIAUserToSolrTransformer extends UserToSolrTransformer
{
    @Value("${foia.portalserviceprovider.directory.name}")
    private String directoryName;

    private SpringContextHolder acmContextHolder;

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmUser in)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String userPrefix = prefixTrailingDot(ldapSyncConfig.getUserPrefix());

        SolrAdvancedSearchDocument solr;

        solr = super.toSolrAdvancedSearch(in);

        if (solr != null)
        {
            // set hidden_b to true if user has portal prefix
            if (solr.getObject_id_s().startsWith(userPrefix))
            {
                solr.setHidden_b(true);
            }
        }
        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmUser in)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String userPrefix = prefixTrailingDot(ldapSyncConfig.getUserPrefix());

        SolrDocument solr;

        solr = super.toSolrQuickSearch(in);

        if (solr != null)
        {
            // set hidden_b to true if user has portal prefix
            if (solr.getObject_id_s().startsWith(userPrefix))
            {
                solr.setHidden_b(true);
            }
        }
        return solr;
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }
}