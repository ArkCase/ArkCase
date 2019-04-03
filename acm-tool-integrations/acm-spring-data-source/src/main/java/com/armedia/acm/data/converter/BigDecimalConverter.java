package com.armedia.acm.data.converter;

/*-
 * #%L
 * ACM Service: Media engine
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

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import java.math.BigDecimal;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
@Converter
public class BigDecimalConverter implements AttributeConverter<BigDecimal, String>
{
    @Override
    public String convertToDatabaseColumn(BigDecimal duration)
    {
        return duration == null || "null".equalsIgnoreCase(duration.toString()) ? "0" : duration.toString();
    }

    @Override
    public BigDecimal convertToEntityAttribute(String durationAsString)
    {
        return durationAsString == null || "null".equalsIgnoreCase(durationAsString) ? new BigDecimal("0")
                : new BigDecimal(durationAsString);
    }
}
