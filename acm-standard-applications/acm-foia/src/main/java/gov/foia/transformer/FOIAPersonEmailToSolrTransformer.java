package gov.foia.transformer;

import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.service.PersonEmailToSolrTransformer;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.foia.model.FOIAPerson;

/**
 * Created by ana.serafimoska
 */
public class FOIAPersonEmailToSolrTransformer extends PersonEmailToSolrTransformer
{
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Person in)
    {
        SolrAdvancedSearchDocument solr = null;
        if (in instanceof FOIAPerson)
        {
            FOIAPerson foiaPerson = (FOIAPerson) in;
            solr = super.toSolrAdvancedSearch(foiaPerson);
            if (solr != null)
            {
                solr.setObject_sub_type_s("FOIA_PERSON");
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
        return FOIAPerson.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return FOIAPerson.class;
    }
}
