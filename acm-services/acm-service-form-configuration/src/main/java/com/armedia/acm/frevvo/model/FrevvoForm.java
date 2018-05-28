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

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class FrevvoForm
{

    private String mode;
    private Long containerId;
    private Long folderId;
    private String docUriParameters;

    @XmlElement(name = "mode")
    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    @XmlElement(name = "containerId")
    public Long getContainerId()
    {
        return containerId;
    }

    public void setContainerId(Long containerId)
    {
        this.containerId = containerId;
    }

    @XmlElement(name = "folderId")
    public Long getFolderId()
    {
        return folderId;
    }

    public void setFolderId(Long folderId)
    {
        this.folderId = folderId;
    }

    @XmlElement(name = "docUriParameters")
    public String getDocUriParameters()
    {
        return docUriParameters;
    }

    public void setDocUriParameters(String docUriParameters)
    {
        this.docUriParameters = docUriParameters;
    }
}
