package com.armedia.acm.services.tag.dao;

/*-
 * #%L
 * ACM Service: Tag
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
public class AssociatedTagDao extends AcmAbstractDao<AcmAssociatedTag>
{

    @Override
    protected Class<AcmAssociatedTag> getPersistenceClass()
    {
        return AcmAssociatedTag.class;
    }

    public List<AcmAssociatedTag> getAcmAssociatedTagByTagIdAndObjectIdAndType(Long tagId, Long objectId, String objectType)
    {

        Query query = getEm().createQuery(
                "SELECT assTag FROM AcmAssociatedTag assTag " +
                        "WHERE assTag.tag.id =:tagId " +
                        "AND assTag.parentId =:objectId " +
                        "AND assTag.parentType =:objectType ");

        query.setParameter("tagId", tagId);
        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        List<AcmAssociatedTag> resultList = query.getResultList();

        return resultList;
    }

    @Transactional
    public int deleteAssociateTag(Long tagId, Long objectId, String objectType) throws SQLException
    {
        AcmAssociatedTag result = getAcmAssociatedTagByTagIdAndObjectIdAndType(tagId, objectId, objectType).get(0);
        int rowCount = 0;
        if (result != null)
        {
            getEm().remove(result);
            rowCount = 1;
        }
        return rowCount;
    }

    public List<AcmAssociatedTag> getAcmAssociatedTagsByObjectIdAndType(Long objectId, String objectType) throws AcmObjectNotFoundException
    {

        Query query = getEm().createQuery(
                "SELECT assTag FROM AcmAssociatedTag assTag " +
                        "WHERE assTag.parentId =:objectId " +
                        "AND assTag.parentType =:objectType ");

        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        List<AcmAssociatedTag> resultList = query.getResultList();

        if (resultList.isEmpty())
        {
            throw new AcmObjectNotFoundException("ASSOCIATED-TAG", null, "Associated Tags not found", null);
        }

        return resultList;
    }

}
