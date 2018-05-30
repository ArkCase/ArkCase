package com.armedia.acm.data.converter;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.util.UUID;

/**
 * {@link UUID} to {@link String} converter.
 * Postgres has issues with {@link UUID} fileds, so we convert them to {@link String}
 * Created by Bojan Milenkoski on 18.1.2016.
 */
@Converter
public class UUIDToStringConverter implements AttributeConverter<UUID, String>
{
    @Override
    public String convertToDatabaseColumn(UUID attribute)
    {
        if (attribute == null)
        {
            return null;
        }
        return attribute.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String dbData)
    {
        if (dbData == null)
        {
            return null;
        }
        return UUID.fromString(dbData);
    }
}
