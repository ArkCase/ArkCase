package com.armedia.acm.plugins.admin.model;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.services.transcribe.annotation.ConfigurationProperties;
import com.armedia.acm.services.transcribe.annotation.ConfigurationProperty;

@ConfigurationProperties(path = "${user.home}/.arkcase/acm/ecmFileService.properties")
public class OCRConfiguration
{

    @ConfigurationProperty(key = "ecm.viewer.snowbound.enableOCR")
    private boolean enableOCR;

    public boolean isEnableOCR()
    {
        return enableOCR;
    }

    public void setEnableOCR(boolean enableOCR)
    {
        this.enableOCR = enableOCR;
    }

    @Override
    public String toString()
    {
        return "OCR Configuration{" +
                "enableOCR=" + enableOCR +
                '}';
    }
}
