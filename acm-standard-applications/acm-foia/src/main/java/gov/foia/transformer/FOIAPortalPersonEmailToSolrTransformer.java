package gov.foia.transformer;

import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.foia.model.PortalFOIAPerson;

/**
 * Created by ana.serafimoska
 */
public class FOIAPortalPersonEmailToSolrTransformer extends FOIAPersonEmailToSolrTransformer
{
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Person in)
    {
        SolrAdvancedSearchDocument solr = null;
        if (in instanceof PortalFOIAPerson)
        {
            PortalFOIAPerson portalFOIAPerson = (PortalFOIAPerson) in;
            solr = super.toSolrAdvancedSearch(portalFOIAPerson);
            if (solr != null)
            {
                solr.setObject_sub_type_s("PORTAL_FOIA_PERSON");
            }
            return solr;
        }
        else
        {
            log.error("Could not send to advanced search class name {}!.", in.getClass().getName());
        }
        throw new RuntimeException("Could not send to advanced search class name " + in.getClass().getName() + "!.");

    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return PortalFOIAPerson.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return PortalFOIAPerson.class;
    }
}
