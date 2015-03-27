package com.armedia.acm.services.tag.dao;

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
public class AssociatedTagDao extends AcmAbstractDao<AcmAssociatedTag> {

    @Override
    protected Class<AcmAssociatedTag> getPersistenceClass() {
        return AcmAssociatedTag.class;
    }

    public List<AcmAssociatedTag> getAcmAssociatedTagByTagIdAndObjectIdAndType(Long tagId, Long objectId, String objectType)  {

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


    public AcmAssociatedTag getAssociatedTagByTagIdAndObjectIdAndType (Long tagId, Long objectId, String objectType){
        Query selectQuery = getEm().createQuery("SELECT assTag FROM AcmAssociatedTag assTag " +
                "WHERE assTag.tag.id =:tagId " +
                "AND assTag.objectId=:objectId " +
                "AND assTag.subscriptionObjectType=:objectType");

        selectQuery.setParameter("tagId",tagId);
        selectQuery.setParameter("objectId",objectId);
        selectQuery.setParameter("objectType",objectType);

        AcmAssociatedTag result;

        result = (AcmAssociatedTag) selectQuery.getResultList().get(0);
        return  result;
    }


    @Transactional
    public int deleteAssociateTag( Long tagId, Long objectId, String objectType ) throws SQLException {
        AcmAssociatedTag result = getAssociatedTagByTagIdAndObjectIdAndType(tagId,objectId,objectType);
        int rowCount = 0;
        if(result!=null){;
            getEm().remove(result);
            rowCount = 1;
        }
        return rowCount;
    }

    public List<AcmAssociatedTag> getAcmAssociatedTagsByObjectIdAndType(Long objectId, String objectType) throws AcmObjectNotFoundException {

        Query query = getEm().createQuery(
                "SELECT assTag FROM AcmAssociatedTag assTag " +
                        "WHERE assTag.parentId =:objectId " +
                        "AND assTag.parentType =:objectType ");

        query.setParameter("objectId", objectId);
        query.setParameter("objectType", objectType);
        List<AcmAssociatedTag> resultList = query.getResultList();


        if( resultList.isEmpty()){
            throw new AcmObjectNotFoundException("ASSOCIATED-TAG", null, "No Associated Tags are found", null);
        }


        return resultList;
    }

}
