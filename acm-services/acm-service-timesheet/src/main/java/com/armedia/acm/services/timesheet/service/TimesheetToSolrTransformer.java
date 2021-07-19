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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.DESCRIPTION_NO_HTML_TAGS_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;

import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
import com.armedia.acm.services.timesheet.dao.AcmTimesheetDao;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public class TimesheetToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmTimesheet>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private AcmTimesheetDao acmTimesheetDao;

    @Override
    public List<AcmTimesheet> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getAcmTimesheetDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmTimesheet in)
    {
        SolrAdvancedSearchDocument solrDoc = new SolrAdvancedSearchDocument();
        LOG.debug("Creating Solr advanced search document for TIMESHEET.");

        mapRequiredProperties(solrDoc, in.getId(), in.getCreator(), in.getCreated(), in.getModifier(), in.getModified(),
                TimesheetConstants.OBJECT_TYPE, in.getTimesheetNumber());

        mapAdditionalProperties(in, solrDoc.getAdditionalProperties());

        return solrDoc;
    }

    @Override
    public void mapAdditionalProperties(AcmTimesheet in, Map<String, Object> additionalProperties)
    {
        additionalProperties.put(TITLE_PARSEABLE, in.getTitle());
        additionalProperties.put(DESCRIPTION_NO_HTML_TAGS_PARSEABLE, in.getDetails());
        additionalProperties.put(STATUS_LCS, in.getStatus());
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
