/**
 * 
 */
package com.armedia.acm.form.config.xml;

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

import com.armedia.acm.form.config.ResolveInformation;
import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class CaseResolveInformation extends ResolveInformation
{

    @XmlElement(name = "caseId")
    @Override
    public Long getId()
    {
        return super.getId();
    }

    @Override
    public void setId(Long id)
    {
        super.setId(id);
    }

    @XmlElement(name = "caseNumber")
    @Override
    public String getNumber()
    {
        return super.getNumber();
    }

    @Override
    public void setNumber(String number)
    {
        super.setNumber(number);
    }

    @XmlElement(name = "changeDate")
    @XmlJavaTypeAdapter(value = DateFrevvoAdapter.class)
    @Override
    public Date getDate()
    {
        return super.getDate();
    }

    @Override
    public void setDate(Date date)
    {
        super.setDate(date);
    }

    @XmlElement(name = "status")
    @Override
    public String getOption()
    {
        return super.getOption();
    }

    @Override
    public void setOption(String option)
    {
        super.setOption(option);
    }

}
