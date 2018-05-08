package com.armedia.acm.services.labels.model;

/*-
 * #%L
 * ACM Service: Labels Service
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
