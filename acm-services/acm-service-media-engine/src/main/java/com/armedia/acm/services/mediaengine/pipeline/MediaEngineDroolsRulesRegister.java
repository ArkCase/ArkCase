package com.armedia.acm.services.mediaengine.pipeline;

/*-
 * #%L
 * ACM Service: Media Engine
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

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEngineDroolsRulesRegister<T, K>
{
    private Map<T, K> rules;

    private T key;
    private K value;

    public MediaEngineDroolsRulesRegister()
    {
        rules = new HashMap<>();
    }

    public void setKey(T key)
    {
        this.key = key;
    }

    public void setValue(K value)
    {
        this.value = value;
    }

    @PostConstruct
    public void addRule()
    {
        rules.put(key, value);
    }

    public void setRules(Map rules)
    {
        this.rules = rules;
    }
}
