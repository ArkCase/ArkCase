package com.armedia.acm.objectonverter.adapter;

/*-
 * #%L
 * Tool Integrations: Object Converter
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

import com.armedia.acm.objectonverter.DateFormats;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFrevvoAdapter extends XmlAdapter<String, Date>
{

    private SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormats.FREVVO_DATE_FORMAT_MARSHAL_UNMARSHAL);

    @Override
    public String marshal(Date date) throws Exception
    {
        return getDateFormat().format(date);
    }

    @Override
    public Date unmarshal(String date) throws Exception
    {
        return getDateFormat().parse(date);
    }

    public SimpleDateFormat getDateFormat()
    {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat)
    {
        this.dateFormat = dateFormat;
    }

}
