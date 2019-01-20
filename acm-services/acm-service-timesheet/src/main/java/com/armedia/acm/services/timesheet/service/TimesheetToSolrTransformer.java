/**
 *
 */
package com.armedia.acm.services.timesheet.service;

/*-
 * #%L
 * ACM Service: Timesheet
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
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-" + TimesheetConstants.OBJECT_TYPE);
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_id_i(in.getId());
        solr.setObject_type_s(TimesheetConstants.OBJECT_TYPE);
        solr.setTitle_parseable(in.getTitle());
        solr.setDescription_no_html_tags_parseable(in.getDetails());
        solr.setName(in.getTimesheetNumber());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setStatus_lcs(in.getStatus());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmTimesheet in)
    {
        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + TimesheetConstants.OBJECT_TYPE);
        solr.setName(in.getTimesheetNumber());
        solr.setTitle_parseable(in.getTitle());
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_id_i(in.getId());
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
