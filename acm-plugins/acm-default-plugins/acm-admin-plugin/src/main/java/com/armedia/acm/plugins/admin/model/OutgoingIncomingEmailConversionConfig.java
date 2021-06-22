package com.armedia.acm.plugins.admin.model;

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
