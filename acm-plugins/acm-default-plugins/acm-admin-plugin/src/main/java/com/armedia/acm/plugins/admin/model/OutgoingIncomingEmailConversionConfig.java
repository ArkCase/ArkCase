package com.armedia.acm.plugins.admin.model;

/*-
 * #%L
 * ACM Default Plugin: admin
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.factory.annotation.Value;

@JsonSerialize(as = OutgoingIncomingEmailConversionConfig.class)
public class OutgoingIncomingEmailConversionConfig
{

    @JsonProperty("convertEmailsToPDF.incomingEmailToPdf")
    @Value("${convertEmailsToPDF.incomingEmailToPdf:false}")
    private Boolean convertIncomingEmailToPdf;

    @JsonProperty("convertEmailsToPDF.outgoingEmailToPdf")
    @Value("${convertEmailsToPDF.outgoingEmailToPdf:false}")
    private Boolean convertOutgoingEmailToPdf;

    public Boolean getConvertIncomingEmailToPdf()
    {
        return convertIncomingEmailToPdf;
    }

    public void setConvertIncomingEmailToPdf(Boolean convertIncomingEmailToPdf)
    {
        this.convertIncomingEmailToPdf = convertIncomingEmailToPdf;
    }

    public Boolean getConvertOutgoingEmailToPdf()
    {
        return convertOutgoingEmailToPdf;
    }

    public void setConvertOutgoingEmailToPdf(Boolean convertOutgoingEmailToPdf)
    {
        this.convertOutgoingEmailToPdf = convertOutgoingEmailToPdf;
    }

}
