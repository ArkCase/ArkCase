/**
 *
 */
package com.armedia.acm.plugins.report.model;

/*-
 * #%L
 * ACM Default Plugin: report
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

import com.armedia.acm.objectonverter.adapter.DateMillisecondAdapter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author riste.tutureski
 */
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Report implements Serializable
{

    private static final long serialVersionUID = -8652555531179054044L;

    private String id;
    private String name;
    private String propertyName;
    private String title;
    private String description;
    private Long fileSize;

    @XmlElement(name = "createdDate")
    @XmlJavaTypeAdapter(value = DateMillisecondAdapter.class)
    private Date created;

    @XmlElement(name = "lastModifiedDate")
    @XmlJavaTypeAdapter(value = DateMillisecondAdapter.class)
    private Date modified;

    private boolean folder;
    private boolean hidden;
    private boolean locked;
    private String path;
    private String propertyPath;
    private String versionId;
    private boolean versioned;
    private String locale;
    private boolean injected;

    @XmlElement(name = "localePropertiesMapEntries")
    private ReportProperties properties;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;

        setPropertyName(null);
    }

    public String getPropertyName()
    {
        if (propertyName == null)
        {
            setPropertyName(null);
        }
        return propertyName;
    }

    public void setPropertyName(String propertyName)
    {
        if (propertyName == null)
        {
            String reportName = getName();
            if (reportName != null)
            {
                int lastDot = reportName.lastIndexOf(".");
                String noExtension = lastDot > 0 ? reportName.substring(0, lastDot) : reportName;

                String goodPropertyName = noExtension.replace(" ", "_");

                if (!goodPropertyName.equals(goodPropertyName.toUpperCase()))
                {
                    // convert camelCase to underscore-separated, e.g. ReportName becoems Report_Name
                    goodPropertyName = goodPropertyName.replaceAll("(?<!^)([a-z])([A-Z])", "$1_$2");
                    goodPropertyName = goodPropertyName.replaceAll("\\+", "_");
                }
                goodPropertyName = goodPropertyName.toUpperCase();

                this.propertyName = goodPropertyName;
            }
        }
        else
        {
            this.propertyName = propertyName;
        }

    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified(Date modified)
    {
        this.modified = modified;
    }

    public boolean isFolder()
    {
        return folder;
    }

    public void setFolder(boolean folder)
    {
        this.folder = folder;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public boolean isLocked()
    {
        return locked;
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;

        setPropertyPath(null);
    }

    public String getPropertyPath()
    {
        if (propertyPath == null)
        {
            setPropertyPath(null);
        }
        return propertyPath;
    }

    public void setPropertyPath(String propertyPath)
    {
        if (propertyPath == null)
        {
            if (getPath() != null)
            {
                this.propertyPath = getPath().replace("/", ":");
            }
        }
        else
        {
            this.propertyPath = propertyPath;
        }
    }

    public String getVersionId()
    {
        return versionId;
    }

    public void setVersionId(String versionId)
    {
        this.versionId = versionId;
    }

    public boolean isVersioned()
    {
        return versioned;
    }

    public void setVersioned(boolean versioned)
    {
        this.versioned = versioned;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public boolean isInjected()
    {
        return injected;
    }

    public void setInjected(boolean injected)
    {
        this.injected = injected;
    }

    public ReportProperties getProperties()
    {
        return properties;
    }

    public void setProperties(ReportProperties properties)
    {
        this.properties = properties;
    }
}
