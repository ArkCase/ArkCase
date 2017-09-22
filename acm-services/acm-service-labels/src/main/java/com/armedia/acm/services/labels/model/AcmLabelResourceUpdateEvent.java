package com.armedia.acm.services.labels.model;

import org.json.JSONObject;
import org.springframework.context.ApplicationEvent;

/**
 * Created by bojan.milenkoski on 07.9.2017
 */
public class AcmLabelResourceUpdateEvent extends ApplicationEvent
{
    private static final long serialVersionUID = 1L;
    private String moduleId;
    private String lang;
    private JSONObject jsonObject;

    public AcmLabelResourceUpdateEvent(Object source)
    {
        super(source);
    }

    public AcmLabelResourceUpdateEvent(Object source, String moduleId, String lang, JSONObject jsonObject)
    {
        super(source);
        this.moduleId = moduleId;
        this.lang = lang;
        this.jsonObject = jsonObject;
    }

    public String getModuleId()
    {
        return moduleId;
    }

    public void setModuleId(String moduleId)
    {
        this.moduleId = moduleId;
    }

    public String getLang()
    {
        return lang;
    }

    public void setLang(String lang)
    {
        this.lang = lang;
    }

    public JSONObject getJsonObject()
    {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }
}
