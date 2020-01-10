package com.armedia.acm.services.timesheet.service;

/*-
 * #%L
 * ACM Service: Timesheet
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.services.timesheet.dao.AcmTimeDao;
import com.armedia.acm.services.timesheet.model.AcmTime;
import com.armedia.acm.services.timesheet.model.TimeConstants;

import java.util.Date;
import java.util.List;

/**
 * @author aleksandar.bujaroski
 */
public class AcmTimeToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmTime>
{
    private AcmTimeDao acmTimeDao;

    @Override
    public List<AcmTime> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getAcmTimeDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmTime in)
    {
        SolrAdvancedSearchDocument solr = new SolrAdvancedSearchDocument();

        solr.setId(in.getId() + "-" + TimeConstants.OBJECT_TYPE);
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_id_i(in.getId());
        solr.setObject_type_s(TimeConstants.OBJECT_TYPE);
        solr.setTitle_parseable(in.getCode());
        solr.setName(in.getCode());

        solr.setCreate_date_tdt(in.getCreated());
        solr.setCreator_lcs(in.getCreator());
        solr.setModified_date_tdt(in.getModified());
        solr.setModifier_lcs(in.getModifier());

        solr.setParent_id_s(Long.toString(in.getObjectId()));
        solr.setParent_type_s(in.getType());
        solr.setParent_ref_s(in.getObjectId() + "-" + in.getType());

        solr.setAdditionalProperty("timesheet_id_i", in.getTimesheet().getId());

        return solr;
    }

    @Override
    public SolrDocument toSolrQuickSearch(AcmTime in)
    {
        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + TimeConstants.OBJECT_TYPE);
        solr.setName(in.getCode());
        solr.setTitle_parseable(in.getCode());
        solr.setObject_id_s(Long.toString(in.getId()));
        solr.setObject_id_i(in.getId());
        solr.setObject_type_s(TimeConstants.OBJECT_TYPE);
        solr.setAuthor_s(in.getCreator());
        solr.setStartDate_s(in.getCreated());

        solr.setAuthor(in.getCreator());
        solr.setCreate_tdt(in.getCreated());
        solr.setModifier_s(in.getModifier());
        solr.setLast_modified_tdt(in.getModified());

        solr.setParent_object_id_s(Long.toString(in.getObjectId()));
        solr.setParent_object_type_s(in.getType());
        solr.setParent_ref_s(in.getObjectId() + "-" + in.getType());

        solr.setAdditionalProperty("timesheet_id_i", in.getTimesheet().getId());

        return solr;
    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return AcmTime.class.equals(acmObjectType);
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return AcmTime.class;
    }

    public AcmTimeDao getAcmTimeDao()
    {
        return acmTimeDao;
    }

    public void setAcmTimeDao(AcmTimeDao acmTimeDao)
    {
        this.acmTimeDao = acmTimeDao;
    }
}
