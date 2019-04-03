package com.armedia.acm.plugins.ecm.model;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.factory.annotation.Value;

@JsonSerialize(as=EcmFileUploaderConfig.class)
public class EcmFileUploaderConfig
{

    @JsonProperty("fileUploader.arkcase.hash.file.identifier")
    @Value("${fileUploader.arkcase.hash.file.identifier}")
    @JsonIgnore
    private String uniqueHashFileIdentifier;

    @JsonProperty("fileUploader.singleChunkFileSizeLimit")
    @Value("${fileUploader.singleChunkFileSizeLimit}")
    private Long singleChunkFileSizeLimit;

    @JsonProperty("fileUploader.uploadFileSizeLimit")
    @Value("${fileUploader.uploadFileSizeLimit}")
    private Long uploadFileSizeLimit;

    @JsonProperty("fileUploader.enableFileChunkUpload")
    @Value("${fileUploader.enableFileChunkUpload}")
    private Boolean enableFileChunkUpload;


    public String getUniqueHashFileIdentifier() {
        return uniqueHashFileIdentifier;
    }

    public Long getSingleChunkFileSizeLimit() {
        return singleChunkFileSizeLimit;
    }

    public void setSingleChunkFileSizeLimit(Long singleChunkFileSizeLimit) {
        this.singleChunkFileSizeLimit = singleChunkFileSizeLimit;
    }

    public Long getUploadFileSizeLimit() {
        return uploadFileSizeLimit;
    }

    public void setUploadFileSizeLimit(Long uploadFileSizeLimit) {
        this.uploadFileSizeLimit = uploadFileSizeLimit;
    }

    public boolean isEnableFileChunkUpload() {
        return enableFileChunkUpload;
    }

    public void setEnableFileChunkUpload(boolean enableFileChunkUpload) {
        this.enableFileChunkUpload = enableFileChunkUpload;
    }

}
