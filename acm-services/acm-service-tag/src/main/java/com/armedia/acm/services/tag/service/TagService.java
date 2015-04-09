package com.armedia.acm.services.tag.service;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.model.TagConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 30.03.2015.
 */
public class TagService {


    private TagDao tagDao;
    private AcmPlugin tagPlugin;

    private transient final Logger log = LoggerFactory.getLogger(getClass());


    public List<AcmTag> getAllTags(){
        List<AcmTag> tags = getTagDao().findAll();
        //If there are no tags in the DB, add predefined tags from property file
        if(tags.isEmpty()){
            List<AcmTag> newTags = prepareTagsFromPropertiesFile();
            for(AcmTag tag: newTags){
                tags.add(getTagDao().save(tag));
            }
        }
        return tags;
    }

    public AcmTag findTag(Long tagId) {
        return getTagDao().find(tagId);
    }

    public  AcmTag saveTag(String name,String desc, String value){

        AcmTag newTag = new AcmTag();
        newTag.setTagText(value);
        newTag.setTagName(name);
        newTag.setTagDescription(desc);

        return getTagDao().save(newTag);
    }

    public AcmTag getTagByTextOrDescOrName(String text, String desc, String name) {
        return  getTagDao().getTagByTextOrDescOrName(text, desc, name);
    }

    public void removeTag( Long tagId ) throws SQLException {
        AcmTag tag = getTagDao().find(tagId);
        getTagDao().deleteTag(tag);
    }

    public void removeTag( AcmTag tag ) throws SQLException {
        getTagDao().deleteTag(tag);
    }

    public AcmTag updateTag( Long tagId, String name, String text, String desc) throws SQLException {
        AcmTag tagForUpdate = getTagDao().find(tagId);
        tagForUpdate.setTagName(name);
        tagForUpdate.setTagText(text);
        tagForUpdate.setTagDescription(desc);
        return getTagDao().save(tagForUpdate);
    }

    private List<AcmTag> prepareTagsFromPropertiesFile(){
        List<AcmTag> tags = new ArrayList<>();
        String jsonTagsString = null;
        if(getTagPlugin().getPluginProperties().containsKey(TagConstants.TAGS)) {
             jsonTagsString = (String) getTagPlugin().getPluginProperties().get(TagConstants.TAGS);
        }
        JSONArray allTagsJsonArray = new JSONArray(jsonTagsString);
        for(int i = 0; i < allTagsJsonArray.length(); i++ ){
            JSONObject tagObject = allTagsJsonArray.getJSONObject(i);
            tags.add(prepareTagFromJsonObject(tagObject));
        }

        return tags;
    }
    private AcmTag prepareTagFromJsonObject(JSONObject jsonObject){
        AcmTag tag = new AcmTag();
        tag.setTagName(jsonObject.getString(TagConstants.TAG_NAME));
        tag.setTagDescription(jsonObject.getString(TagConstants.TAG_DESC));
        tag.setTagText(jsonObject.getString(TagConstants.TAG_VALUE));
        return tag;
    }

    public AcmPlugin getTagPlugin() {
        return tagPlugin;
    }

    public void setTagPlugin(AcmPlugin tagPlugin) {
        this.tagPlugin = tagPlugin;
    }

    public TagDao getTagDao() {
        return tagDao;
    }

    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }
}
