/**
 *
 */
package com.armedia.acm.services.timesheet.service;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class TimesheetToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmTimesheet>
{

    private AcmTimesheetDao acmTimesheetDao;

    @Override
    public List<AcmTimesheet> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getAcmTimesheetDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmTimesheet in)
    {
        // No need Advanced Search for now. We are not adding the Timesheet to the Advanced Search
        // No implementation needed
        return null;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmTimesheet in)
    {
        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + TimesheetConstants.OBJECT_TYPE);
        solr.setName(in.getTitle());
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_type_s(TimesheetConstants.OBJECT_TYPE);
        solr.setAuthor_s(in.getUser().getUserId());
        solr.setStartDate_s(in.getStartDate());
        solr.setEndDate_s(in.getEndDate());

        if (in.getContainer() != null)
        {
            solr.setParent_ref_s(in.getContainer().getContainerObjectId() + "-" + in.getContainer().getContainerObjectType());
        }

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setStatus_s(in.getStatus());

        return solr;
    }

    @Override
    public SolrAdvancedSearchDocument toContentFileIndex(AcmTimesheet in)
    {
        // No implementation needed
        return null;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmTimesheet.class.equals(acmObjectType);
    }

    public AcmTimesheetDao getAcmTimesheetDao()
    {
        return acmTimesheetDao;
    }

    public void setAcmTimesheetDao(AcmTimesheetDao acmTimesheetDao)
    {
        this.acmTimesheetDao = acmTimesheetDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmTimesheet.class;
    }

}
