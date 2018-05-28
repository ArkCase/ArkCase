package com.armedia.acm.plugins.casefile.service;

/*-
 * #%L
 * ACM Default Plugin: Case File
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

import com.armedia.acm.plugins.casefile.dao.DispositionDao;
import com.armedia.acm.plugins.casefile.model.Disposition;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.search.model.solr.SolrDocument;
import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;

import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 09.03.2015.
 */
public class DispositionToSolrTransformer implements AcmObjectToSolrDocTransformer<Disposition>
{

    private DispositionDao dispositionDao;

    @Override
    public List<Disposition> getObjectsModifiedSince(Date lastModified, int start, int pageSize)
    {
        return getDispositionDao().findModifiedSince(lastModified, start, pageSize);
    }

    @Override
    public SolrAdvancedSearchDocument toSolrAdvancedSearch(Disposition in)
    {
        return null;
    }

    @Override
    public SolrDocument toSolrQuickSearch(Disposition in)
    {

        SolrDocument solr = new SolrDocument();

        solr.setId(in.getId() + "-" + in.getObjectType());
        solr.setObject_id_s(in.getId() + "");
        solr.setObject_type_s(in.getObjectType());

        solr.setCreate_tdt(in.getCreated());
        solr.setAuthor(in.getCreator());
        solr.setLast_modified_tdt(in.getModified());
        solr.setModifier_s(in.getModifier());

        solr.setDisposition_type_s(in.getDispositionType());
        if (in.getExistingCaseNumber() != null)
        {
            solr.setTarget_object_number_s(in.getExistingCaseNumber());
        }

        return solr;

    }

    @Override
    public boolean isAcmObjectTypeSupported(Class acmObjectType)
    {
        return Disposition.class.equals(acmObjectType);
    }

    public DispositionDao getDispositionDao()
    {
        return dispositionDao;
    }

    public void setDispositionDao(DispositionDao dispositionDao)
    {
        this.dispositionDao = dispositionDao;
    }

    @Override
    public Class<?> getAcmObjectTypeSupported()
    {
        return Disposition.class;
    }
}
