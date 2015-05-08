package com.armedia.acm.services.tag.service;

import com.armedia.acm.auth.AcmAuditPropertyInterceptor;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.model.TagConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.mule.api.annotations.expressions.Mule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 31.03.2015.
 */
public class AssociatedTagService {

    private TagDao tagDao;
    private AssociatedTagDao associatedTagDao;
    private AcmPlugin tagPlugin;
    private ExecuteSolrQuery executeSolrQuery;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public List<AcmAssociatedTag> getAcmAssociatedTagByTagIdAndObjectIdAndType(Long tagId, Long  objectId, String objectType) {
        return getAssociatedTagDao().getAcmAssociatedTagByTagIdAndObjectIdAndType(tagId,objectId,objectType);
    }

    public AcmAssociatedTag getAssociatedTagByTagIdAndObjectIdAndType(Long tagId, Long objectId,String objectType){
        return getAssociatedTagDao().getAcmAssociatedTagByTagIdAndObjectIdAndType(tagId, objectId, objectType).get(TagConstants.ZERO);
    }

    public int removeAssociatedTag(Long tagId, Long objectId, String objectType) throws SQLException {
        AcmAssociatedTag acmAssociatedTag = getAssociatedTagDao().getAcmAssociatedTagByTagIdAndObjectIdAndType(tagId,objectId,objectType).get(TagConstants.ZERO);
        return getAssociatedTagDao().deleteAssociateTag(tagId,objectId,objectType);
    }

    public List<AcmAssociatedTag> getAcmAssociatedTagsByObjectIdAndType(Long objectId, String objectType, Authentication auth) throws AcmObjectNotFoundException {
        JSONObject associatedTags = findTagsInSolrByParentTypeAndParentId(objectType,objectId, auth );
        return prepareTagList(objectType,objectId,associatedTags);
    }

    private JSONObject findTagsInSolrByParentTypeAndParentId(String objectType, Long objectId, Authentication auth) throws AcmObjectNotFoundException {

        Map<String, Object> properties =  getTagPlugin().getPluginProperties();
        String predefinedQuery = (String) properties.get(TagConstants.SOLR_QUERY_GET_ASSOCIATED_TAG_BY_OBJECT_ID_AND_OBJECT_TYPE);
        predefinedQuery = predefinedQuery.replace(TagConstants.SOLR_PLACEHOLDER_PARENT_TYPE,objectType);
        predefinedQuery = predefinedQuery.replace(TagConstants.SOLR_PLACEHOLDER_PARENT_ID, Long.toString(objectId));
        String solrResponseJsonString = null;
        try{
            solrResponseJsonString = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.QUICK_SEARCH,
                    predefinedQuery, TagConstants.FIRST_ROW, TagConstants.MAX_ROWS, TagConstants.SORT);
        }catch ( MuleException e ) {
            if(log.isErrorEnabled()){
                log.error("Mule exception occurred while performing quick search to fetch tags for object['" + objectType + "]:[" + objectId + "]",e);
            }
            throw new AcmObjectNotFoundException(TagConstants.OBJECT_TYPE,null,"Mule exception occurred while performing quick search to fetch tags for object['" + objectType + "]:[" + objectId + "]",e);
        }
        return new JSONObject(solrResponseJsonString);
    }

    private List<AcmAssociatedTag> prepareTagList(String objectType, Long objectId,JSONObject solrResponse) throws AcmObjectNotFoundException {

        List<AcmAssociatedTag> associatedTags = new ArrayList<>();
        JSONObject responseBody = solrResponse.getJSONObject(TagConstants.SOLR_RESPONSE_BODY);
        JSONArray docsList = responseBody.getJSONArray(TagConstants.SOLR_RESPONSE_DOCS);
        if(docsList.length()==0) {
            if(log.isErrorEnabled()){
                log.error("No tags associated to the object['" + objectType + "]:[" + objectId + "]");
            }
            throw new AcmObjectNotFoundException(objectType,objectId,"no such object to subscribe to",null);
        }
        for(int i = 0; i<docsList.length(); i++){
            String associatedTagId = docsList.getJSONObject(i).getString(TagConstants.SOLR_ID).split(TagConstants.SOLR_ID_SPLITER)[TagConstants.ZERO];
            AcmAssociatedTag associatedTag = getAssociatedTagDao().find(Long.parseLong(associatedTagId));
            if (associatedTag != null) {
                associatedTags.add(associatedTag);
            }
        }
        return associatedTags;
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

    public AcmPlugin getTagPlugin() {
        return tagPlugin;
    }

    public void setTagPlugin(AcmPlugin tagPlugin) {
        this.tagPlugin = tagPlugin;
    }

    public ExecuteSolrQuery getExecuteSolrQuery() {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery) {
        this.executeSolrQuery = executeSolrQuery;
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
