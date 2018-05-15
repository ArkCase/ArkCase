/**
 * 
 */
package com.armedia.acm.form.project.model.xml;

/*-
 * #%L
 * ACM Forms: Project
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
public class ProjectReview
{

    private String text;
    private String yesNo;
    private String inits;

    @XmlElement(name = "sectionFiveTable1Title")
    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    @XmlElement(name = "sectionFiveTable1YesNo")
    public String getYesNo()
    {
        return yesNo;
    }

    public void setYesNo(String yesNo)
    {
        this.yesNo = yesNo;
    }

    @XmlElement(name = "sectionFiveTable1Inits")
    public String getInits()
    {
        return inits;
    }

    public void setInits(String inits)
    {
        this.inits = inits;
    }

}
