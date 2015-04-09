package com.armedia.acm.services.tag.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by marjan.stefanoski on 31.03.2015.
 */
public class AssociatedTagService {

    private TagDao tagDao;
    private AssociatedTagDao associatedTagDao;


    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public List<AcmAssociatedTag> getAcmAssociatedTagByTagIdAndObjectIdAndType(Long tagId, Long  objectId, String objectType) {
        return getAssociatedTagDao().getAcmAssociatedTagByTagIdAndObjectIdAndType(tagId,objectId,objectType);
    }
    public AcmAssociatedTag getAssociatedTagByTagIdAndObjectIdAndType(Long tagId, Long objectId,String objectType){
        return getAssociatedTagDao().getAssociatedTagByTagIdAndObjectIdAndType(tagId,objectId,objectType);
    }

    public int removeAssociatedTag(Long tagId, Long objectId, String objectType) throws SQLException {
        AcmAssociatedTag acmAssociatedTag = getAssociatedTagDao().getAssociatedTagByTagIdAndObjectIdAndType(tagId,objectId,objectType);
        return getAssociatedTagDao().deleteAssociateTag(tagId,objectId,objectType);
    }

    public List<AcmAssociatedTag> getAcmAssociatedTagsByObjectIdAndType(Long objectId, String objectType) throws AcmObjectNotFoundException {
        return getAcmAssociatedTagsByObjectIdAndType(objectId, objectType);

    }

    public int removeAssociatedTag(AcmAssociatedTag acmAssociatedTag) throws SQLException {
        return getAssociatedTagDao().deleteAssociateTag(acmAssociatedTag.getTag().getId(), acmAssociatedTag.getParentId(),acmAssociatedTag.getParentType());
    }

    public AcmAssociatedTag saveAssociateTag(String objectType, Long objectId, AcmTag tag) {

        //TODO check if exists objectId with objectType if not throw exception

        AcmAssociatedTag acmAssociatedTag = new AcmAssociatedTag();
        acmAssociatedTag.setParentType(objectType);
        acmAssociatedTag.setParentId(objectId);
        acmAssociatedTag.setTag(tag);
        return getAssociatedTagDao().save(acmAssociatedTag);
    }

    public AcmAssociatedTag saveAssociateTag(String objectType, Long objectId, Long tagId) throws AcmObjectNotFoundException {

        AcmAssociatedTag acmAssociatedTag = new AcmAssociatedTag();
        acmAssociatedTag.setParentType(objectType);
        acmAssociatedTag.setParentId(objectId);
        AcmTag tag;
        try {
            tag = getTagDao().find(tagId);
        } catch ( Exception e ) {
            if(log.isErrorEnabled()){
                log.error("Not existing",e);
            }
            throw new AcmObjectNotFoundException(AcmTag.OBJECT_TYPE,tagId,"",e);
        }
        acmAssociatedTag.setTag(tag);
        AcmAssociatedTag newAssociatedTag = getAssociatedTagDao().save(acmAssociatedTag);
        return newAssociatedTag;
    }

    public TagDao getTagDao() {
        return tagDao;
    }

    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    public AssociatedTagDao getAssociatedTagDao() {
        return associatedTagDao;
    }

    public void setAssociatedTagDao(AssociatedTagDao associatedTagDao) {
        this.associatedTagDao = associatedTagDao;
    }
}
