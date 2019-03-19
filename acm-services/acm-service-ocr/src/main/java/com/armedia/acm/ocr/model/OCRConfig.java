package com.armedia.acm.ocr.model;

/*-
 * #%L
 * ACM Services: Optical character recognition via Tesseract
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

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OCRConfig
{
    @JsonProperty("ocr.excludedFileTypes")
    @Value("${ocr.excludedFileTypes}")
    private String excludedFileTypesString;

    public List<String> getExcludedFileTypes()
    {
        return Arrays.stream(excludedFileTypesString.split(",")).collect(Collectors.toList());
    }

    public String getExcludedFileTypesString()
    {
        return excludedFileTypesString;
    }

    public void setExcludedFileTypesString(String excludedFileTypesString)
    {
        this.excludedFileTypesString = excludedFileTypesString;
    }
}
