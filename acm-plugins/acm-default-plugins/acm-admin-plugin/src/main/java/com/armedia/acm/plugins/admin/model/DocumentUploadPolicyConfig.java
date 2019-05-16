package com.armedia.acm.plugins.admin.model;

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
