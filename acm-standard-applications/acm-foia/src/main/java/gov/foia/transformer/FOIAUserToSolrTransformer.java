package gov.foia.transformer;

import static com.armedia.acm.services.users.model.ldap.MapperUtils.prefixTrailingDot;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.service.ldap.UserToSolrTransformer;
import com.armedia.acm.spring.SpringContextHolder;

import org.springframework.beans.factory.annotation.Value;

import java.util.stream.Collectors;

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

        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();
        solr.setId(in.getUserId() + "-USER");
        solr.setObject_id_s(in.getUserId() + "");
        solr.setObject_type_s("USER");
        solr.setName(in.getFullName());
        solr.setFirst_name_lcs(in.getFirstName());
        solr.setLast_name_lcs(in.getLastName());
        solr.setEmail_lcs(in.getMail());

        solr.setTitle_parseable(in.getFullName());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setModified_date_tdt(in.getModified());

        solr.setStatus_lcs(in.getUserState().name());

        // Add groups
        solr.setGroups_id_ss(in.getGroupNames().count() == 0 ? null : in.getGroupNames().collect(Collectors.toList()));

        solr.setAdditionalProperty("directory_name_s", in.getUserDirectoryName());
        solr.setAdditionalProperty("country_s", in.getCountry());
        solr.setAdditionalProperty("country_abbreviation_s", in.getCountryAbbreviation());
        solr.setAdditionalProperty("department_s", in.getDepartment());
        solr.setAdditionalProperty("company_s", in.getCompany());
        solr.setAdditionalProperty("title_s", in.getTitle());
        solr.setAdditionalProperty("name_partial", in.getFullName());
        solr.setAdditionalProperty("name_lcs", in.getFullName());

        // TODO find a way to add Organization
        // TODO find a way to add Application Title
        // TODO find a way to add Location

        // set hidden_b to true if user has portal prefix
        if (solr.getObject_id_s().startsWith(userPrefix))
        {
            solr.setHidden_b(true);
        }

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmUser in)
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String userPrefix = prefixTrailingDot(ldapSyncConfig.getUserPrefix());

        SolrDocument solr = new SolrDocument();
        solr.setName(in.getFullName());
        solr.setTitle_parseable(in.getFullName());
        solr.setObject_id_s(in.getUserId() + "");
        solr.setObject_type_s("USER");
        solr.setId(in.getUserId() + "-USER");

        solr.setCreate_tdt(in.getCreated());
        solr.setLast_modified_tdt(in.getModified());

        solr.setStatus_s(in.getUserState().name());
        solr.setAdditionalProperty("name_partial", in.getFullName());
        solr.setAdditionalProperty("name_lcs", in.getFullName());

        // set hidden_b to true if user has portal prefix
        if (solr.getObject_id_s().startsWith(userPrefix))
        {
            solr.setHidden_b(true);
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