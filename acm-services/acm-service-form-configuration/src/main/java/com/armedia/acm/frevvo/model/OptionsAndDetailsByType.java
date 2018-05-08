/**
 * 
 */
package com.armedia.acm.frevvo.model;

/*-
 * #%L
 * ACM Service: Form Configuration
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

import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class OptionsAndDetailsByType
{

    private Map<String, Options> optionsByType;
    private Map<String, Map<String, Details>> optionsDetailsByType;

    public Map<String, Options> getOptionsByType()
    {
        return optionsByType;
    }

    public void setOptionsByType(Map<String, Options> optionsByType)
    {
        this.optionsByType = optionsByType;
    }

    public Map<String, Map<String, Details>> getOptionsDetailsByType()
    {
        return optionsDetailsByType;
    }

    public void setOptionsDetailsByType(
            Map<String, Map<String, Details>> optionsDetailsByType)
    {
        this.optionsDetailsByType = optionsDetailsByType;
    }
}
