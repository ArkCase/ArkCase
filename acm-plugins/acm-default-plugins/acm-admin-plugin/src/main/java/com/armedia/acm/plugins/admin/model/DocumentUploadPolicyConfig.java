package com.armedia.acm.plugins.admin.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

@JsonSerialize(as = DocumentUploadPolicyConfig.class)
public class DocumentUploadPolicyConfig
{
    @JsonProperty("document.upload.policy.convertHtmlToPdf")
    @Value("${document.upload.policy.convertHtmlToPdf:false}")
    private Boolean convertHtmlToPdf;

    @JsonProperty("document.upload.policy.convertMsgToPdf")
    @Value("${document.upload.policy.convertMsgToPdf:false}")
    private Boolean convertMsgToPdf;

    @JsonProperty("document.upload.policy.convertEmlToPdf")
    @Value("${document.upload.policy.convertEmlToPdf:false}")
    private Boolean convertEmlToPdf;

    public Boolean getConvertHtmlToPdf()
    {
        return convertHtmlToPdf;
    }

    public void setConvertHtmlToPdf(Boolean convertHtmlToPdf)
    {
        this.convertHtmlToPdf = convertHtmlToPdf;
    }

    public Boolean getConvertMsgToPdf()
    {
        return convertMsgToPdf;
    }

    public void setConvertMsgToPdf(Boolean convertMsgToPdf)
    {
        this.convertMsgToPdf = convertMsgToPdf;
    }

    public Boolean getConvertEmlToPdf()
    {
        return convertEmlToPdf;
    }

    public void setConvertEmlToPdf(Boolean convertEmlToPdf)
    {
        this.convertEmlToPdf = convertEmlToPdf;
    }
}
